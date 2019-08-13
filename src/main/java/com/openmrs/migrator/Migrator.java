package com.openmrs.migrator;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.services.impl.MySQLProps;
import com.openmrs.migrator.core.utilities.ConsoleUtils;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "migrator", mixinStandardHelpOptions = true, helpCommand = true)
public class Migrator implements Callable<Optional<Void>> {

  private PDIService pdiService;

  private FileIOUtilities fileIOUtilities;

  private BootstrapService bootstrapService;

  private DataBaseService dataBaseService;

  private SettingsService settingsService;

  private Console console;

  private String[] jobs = {"pdiresources/jobs/merge-patient-job.kjb"};

  private Path settingProperties = Paths.get(SettingsService.SETTINGS_PROPERTIES);

  private List<String> dirList =
      Arrays.asList(
          "input/",
          "output/",
          "config/",
          "pdiresources/",
          "pdiresources/transformations/",
          "pdiresources/jobs/");

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
  public Optional<Void> call() throws IOException, SQLException, SettingsException {

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
    try {
      for (String t : jobs) {

        InputStream xml = fileIOUtilities.getResourceAsStream(t);
        pdiService.runJob(xml);
      }
    } catch (SettingsException e) {
      // Do nothing kettle prints stack trace
    }
  }

  private void executeSetupCommand() throws IOException, SQLException, SettingsException {
    List<String> pdiFiles = new ArrayList<>();

    pdiFiles.add("pdiresources/jobs/merge-patient-job.kjb");
    pdiFiles.add("pdiresources/jobs/validations.kjb");
    pdiFiles.add("pdiresources/transformations/merge-patient.ktr");
    pdiFiles.add("pdiresources/transformations/validate-concepts.ktr");
    pdiFiles.add("pdiresources/transformations/validate-encounter-types.ktr");
    pdiFiles.add("pdiresources/transformations/validate-forms.ktr");
    pdiFiles.add("pdiresources/transformations/validate-order-types.ktr");
    pdiFiles.add("pdiresources/transformations/validate-patient-identifier-types.ktr");
    pdiFiles.add("pdiresources/transformations/validate-person-attribute-types.ktr");
    pdiFiles.add("pdiresources/transformations/validate-programs.ktr");
    pdiFiles.add("pdiresources/transformations/validate-program-workflows.ktr");
    pdiFiles.add("pdiresources/transformations/validate-program-workflow-states.ktr");
    pdiFiles.add("pdiresources/transformations/validate-relationship-types.ktr");
    pdiFiles.add("pdiresources/transformations/validate-roles.ktr");
    pdiFiles.add("pdiresources/transformations/validate-scheduler-task-config.ktr");
    pdiFiles.add("pdiresources/transformations/validate-visit-attribute-types.ktr");
    pdiFiles.add("pdiresources/transformations/validate-visit-types.ktr");
    pdiFiles.add(SettingsService.SETTINGS_PROPERTIES);

    bootstrapService.createDirectoryStructure(dirList);
    bootstrapService.populateDefaultResources(pdiFiles);

    Map<String, String> connDB = ConsoleUtils.readSettingsFromConsole(System.console());

    MySQLProps mysqlConn = getMysqlOptsFromConsoleConn(connDB);
    while (!dataBaseService.testConnection(mysqlConn, false)) {
      System.console()
          .writer()
          .println("You have provided Wrong Connection details, please try again!");
      connDB = ConsoleUtils.readSettingsFromConsole(System.console());
      mysqlConn = getMysqlOptsFromConsoleConn(connDB);
    }

    settingsService.fillConfigFile(settingProperties, connDB);

    MySQLProps mySQLProps = getMysqlConn();
    chooseDatabase(mySQLProps);
  }

  private MySQLProps getMysqlOptsFromConsoleConn(Map<String, String> connDB) {
    return new MySQLProps(
        connDB.get(SettingsService.DB_HOST),
        connDB.get(SettingsService.DB_PORT),
        connDB.get(SettingsService.DB_USER),
        connDB.get(SettingsService.DB_PASS),
        "");
  }

  private void chooseDatabase(MySQLProps mySQLProps) throws IOException, SQLException {
    int choice = ConsoleUtils.startMigrationAproach(console);
    List<String> alreadyLoadedDataBases =
        dataBaseService.oneColumnSQLSelectorCommand(mySQLProps, "show databases", "Database");
    switch (choice) {
      case 1:
        {
          selectFromExistingDatabase(alreadyLoadedDataBases);
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
          MySQLProps db =
              new MySQLProps(
                  mySQLProps.getHost(),
                  mySQLProps.getPort(),
                  mySQLProps.getUsername(),
                  mySQLProps.getPassword(),
                  FilenameUtils.removeExtension(new File(sqlDumpFile).getName()));
          dataBaseService.importDatabaseFile(sqlDumpFile, db);
          alreadyLoadedDataBases =
              dataBaseService.oneColumnSQLSelectorCommand(mySQLProps, "show databases", "Database");
          selectFromExistingDatabase(alreadyLoadedDataBases);
          break;
        }

      default:
        {
          ConsoleUtils.showUnavailableOption(console);
          break;
        }
    }
  }

  private String readAndValidateBackupsFolder() throws IOException {
    String dbsLocation = readBackupsFolderFromConsole();
    List<Path> inputs = fileIOUtilities.listFiles(Paths.get(dbsLocation));
    return ConsoleUtils.chooseDumpFile(console, inputs, dbsLocation);
  }

  private String readBackupsFolderFromConsole() {
    String folder =
        ConsoleUtils.readFromConsole(
            "Folder location containing backups: input/", System.console());
    return StringUtils.isBlank(folder) ? "input/" : folder;
  }

  private void selectFromExistingDatabase(List<String> alreadyLoadedDataBases) throws IOException {
    Optional<String> providedDataBaseName =
        ConsoleUtils.getDatabaseName(console, alreadyLoadedDataBases);
    Optional<String> storedDataBaseName =
        fileIOUtilities.searchForDataBaseNameInSettingsFile(
            providedDataBaseName.get(), settingProperties);

    if (!storedDataBaseName.isPresent()) {
      settingsService.addSettingToConfigFile(
          settingProperties, SettingsService.DB, 2, providedDataBaseName.get());
    }
  }

  private MySQLProps getMysqlConn() throws IOException {
    return new MySQLProps(
        fileIOUtilities.getValueFromConfig(SettingsService.DB_HOST, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.DB_PORT, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.DB_USER, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.DB_PASS, "=", settingProperties),
        fileIOUtilities.getValueFromConfig(SettingsService.DB, "=", settingProperties));
  }

  private void executeRunCommandLogic() throws IOException, SettingsException {
    runAllJobs();
  }
}
