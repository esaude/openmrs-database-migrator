package com.openmrs.migrator;

import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.model.DatabaseProps;
import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.ConsoleUtils;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "migrator", mixinStandardHelpOptions = true, helpCommand = true)
public class Migrator implements Callable<Optional<Void>> {

  public static final String FORM_IMPORT_SCRIPT = "form-import.sh";

  private PDIService pdiService;

  private FileIOUtilities fileIOUtilities;

  private BootstrapService bootstrapService;

  private DataBaseService dataBaseService;

  private SettingsService settingsService;

  private Console console;

  private Path settingProperties = Paths.get(SettingsService.SETTINGS_PROPERTIES);

  private List<String> dirList = Arrays.asList("input/", "output/");

  private final String CONTROL_CENTER = "pdiresources/jobs/control-center.kjb";

  @Option(
      names = {"run"},
      description = "runs the migrator job(s)")
  private boolean run;

  @Option(
      names = {"setup"},
      description = "setups the migrator tool")
  private boolean setup;

  public Migrator() {}

  @Autowired
  public Migrator(
      Console console,
      PDIService pdiService,
      FileIOUtilities fileIOUtilities,
      BootstrapService bootstrapService,
      DataBaseService dataBaseService,
      SettingsService settingsService) {
    this.pdiService = pdiService;
    this.fileIOUtilities = fileIOUtilities;
    this.bootstrapService = bootstrapService;
    this.dataBaseService = dataBaseService;
    this.console = console;
    this.settingsService = settingsService;
  }

  @Override
  public Optional<Void> call()
      throws IOException, SQLException, SettingsException, URISyntaxException,
          InvalidParameterException {
    if (setup) {
      executeSetupCommand();
    }

    if (run) {
      executeRunCommandLogic();
    }

    if (!run && !setup) {
      CommandLine.usage(new Migrator(), System.out);
    }

    return Optional.empty();
  }

  private void runAllJobs() throws IOException, SettingsException {
    try (InputStream is = new FileInputStream(new File(CONTROL_CENTER))) {
      pdiService.runJob(is);
    }
  }

  private void executeSetupCommand()
      throws IOException, URISyntaxException, InvalidParameterException {

    Set<String> set =
        fileIOUtilities.prepareResourceFolder(
            fileIOUtilities.identifyResourceSubFolders(SettingsService.PDI_RESOURCES_DIR + "/"),
            ".k");

    set.addAll(
        fileIOUtilities.prepareResourceFolder(
            fileIOUtilities.identifyResourceSubFolders(SettingsService.PDI_CONFIG + "/"), ".c"));

    set.addAll(
        fileIOUtilities.prepareResourceFolder(
            fileIOUtilities.identifyResourceSubFolders(SettingsService.PDI_PLUGINS_DIR + "/"),
            ".j"));

    fileIOUtilities.copyFileFromResources(SettingsService.SETTINGS_PROPERTIES);

    Map<String, InputStream> map = new HashMap<>();
    for (String s : set) {
      map = fileIOUtilities.getListOfResourceFiles(s);
    }

    bootstrapService.createDirectoryStructure(dirList);
    bootstrapService.populateDefaultResources(map);

    Set<PosixFilePermission> permissions =
        Stream.of(
                PosixFilePermission.OWNER_EXECUTE,
                PosixFilePermission.OWNER_READ,
                PosixFilePermission.OWNER_WRITE)
            .collect(Collectors.toSet());
    fileIOUtilities.changeFilePermission(
        Paths.get(SettingsService.PDI_CONFIG + "/" + FORM_IMPORT_SCRIPT), permissions);
  }

  private DatabaseProps getMysqlOptsFromConsoleConn(Map<String, String> connDB) {
    return new DatabaseProps(
        connDB.get(SettingsService.DB_HOST),
        connDB.get(SettingsService.DB_PORT),
        connDB.get(SettingsService.DB_USER),
        connDB.get(SettingsService.DB_PASS),
        "");
  }

  private void chooseDatabase(DatabaseProps databaseProps) throws IOException, SQLException {
    int choice = ConsoleUtils.startMigrationAproach(console);
    List<String> alreadyLoadedDataBases =
        dataBaseService.oneColumnSQLSelectorCommand(databaseProps, "show databases", "Database");
    switch (choice) {
      case 1:
        {
          selectExistingSourceAndMergeDatabase(alreadyLoadedDataBases);
          break;
        }
      case 2:
        {
          String sqlDumpFile = readAndValidateBackupsFolder();
          while (StringUtils.isBlank(sqlDumpFile)
              || alreadyLoadedDataBases.contains(
                  FilenameUtils.removeExtension(new File(sqlDumpFile).getName()))) {
            sqlDumpFile = readAndValidateBackupsFolder();
          }

          // import database if it doesn't exist in mysql
          DatabaseProps db =
              new DatabaseProps(
                  databaseProps.getHost(),
                  databaseProps.getPort(),
                  databaseProps.getUsername(),
                  databaseProps.getPassword(),
                  FilenameUtils.removeExtension(new File(sqlDumpFile).getName()));
          dataBaseService.importDatabaseFile(sqlDumpFile, db);
          alreadyLoadedDataBases =
              dataBaseService.oneColumnSQLSelectorCommand(
                  databaseProps, "show databases", "Database");
          selectExistingSourceAndMergeDatabase(alreadyLoadedDataBases);
          break;
        }
      default:
        {
          ConsoleUtils.sendMessage(console, "Unavailable Option");
          break;
        }
    }
  }

  private void selectExistingSourceAndMergeDatabase(List<String> alreadyLoadedDataBases)
      throws IOException {
    selectDbFromExistingDatabase(alreadyLoadedDataBases, SettingsService.SOURCE_DB, 2);
    selectDbFromExistingDatabase(alreadyLoadedDataBases, SettingsService.MERGE_DB, 3);
  }

  private String readAndValidateBackupsFolder() throws IOException {
    String dbsLocation = readBackupsFolderFromConsole();
    List<Path> inputs = fileIOUtilities.listFiles(Paths.get(dbsLocation));
    return ConsoleUtils.chooseDumpFile(console, inputs, dbsLocation);
  }

  private String readBackupsFolderFromConsole() {
    String folder =
        ConsoleUtils.readFromConsole("Folder location containing backups: input/", console);
    return StringUtils.isBlank(folder) ? "input/" : folder;
  }

  private void selectDbFromExistingDatabase(
      List<String> alreadyLoadedDataBases, String dbCategory, int lineNumber) throws IOException {
    Optional<String> providedDataBaseName =
        ConsoleUtils.getDatabaseName(
            console,
            alreadyLoadedDataBases,
            SettingsService.MERGE_DB.equals(dbCategory) ? "Merge" : "Source");
    Optional<String> storedDataBaseName =
        fileIOUtilities.searchForDataBaseNameInSettingsFile(
            providedDataBaseName.get(), settingProperties);

    if (!storedDataBaseName.isPresent()) {
      settingsService.addSettingToConfigFile(
          settingProperties, dbCategory, lineNumber, providedDataBaseName.get());
    }
  }

  private DatabaseProps getMysqlConn() throws IOException {
    return new DatabaseProps(
        fileIOUtilities.getValueFromConfig(SettingsService.DB_HOST, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.DB_PORT, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.DB_USER, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.DB_PASS, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.SOURCE_DB, "=", settingProperties));
  }

  private void executeRunCommandLogic() throws IOException, SettingsException, SQLException {

    if (fileIOUtilities.isSettingsFilesMissingSomeValue()) {
      Map<String, String> connDB = ConsoleUtils.readSettingsFromConsole(console);

      DatabaseProps mysqlConn = getMysqlOptsFromConsoleConn(connDB);
      while (!dataBaseService.testConnection(mysqlConn, false)) {
        console.writer().println("You have provided Wrong Connection details, please try again!");
        connDB = ConsoleUtils.readSettingsFromConsole(console);
        mysqlConn = getMysqlOptsFromConsoleConn(connDB);
      }

      settingsService.fillConfigFile(settingProperties, connDB);

      DatabaseProps databaseProps = getMysqlConn();
      chooseDatabase(databaseProps);
    }

    if (Files.exists(Paths.get(SettingsService.PDI_RESOURCES_DIR))
        && Files.exists(Paths.get(SettingsService.SETTINGS_PROPERTIES))) {
      runAllJobs();
    } else {
      ConsoleUtils.sendMessage(console, "Run Setup first please!");
    }
  }
}
