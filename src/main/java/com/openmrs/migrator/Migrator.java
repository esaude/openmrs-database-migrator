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
    pdiFiles.add("pdiresources/transformations/merge-patient.ktr");
    pdiFiles.add(SettingsService.SETTINGS_PROPERTIES);

    bootstrapService.createDirectoryStructure(dirList);
    bootstrapService.populateDefaultResources(pdiFiles);

    Map<String, String> connDB = ConsoleUtils.readSettingsFromConsole(System.console());

    settingsService.fillConfigFile(settingProperties, connDB);

    MySQLProps mySQLProps = getMysqlConn();
    mySQLProps.setIncludeDbOntoUrl(false);
    int choice = ConsoleUtils.startMigrationAproach(console);
    List<String> alreadyLoadedDataBases =
        dataBaseService.oneColumnSQLSelectorCommand(mySQLProps, "show databases", "Database");
    mySQLProps.setIncludeDbOntoUrl(true);
    switch (choice) {
      case 1:
        {
          Optional<String> providedDataBaseName = ConsoleUtils.getDatabaseDetaName(console);
          Optional<String> storedDataBaseName =
              fileIOUtilities.searchForDataBaseNameInSettingsFile(
                  providedDataBaseName.get(), settingProperties);

          if (!storedDataBaseName.isPresent()) {
            if (ConsoleUtils.isConnectionIsToBeStored(console)) {
              settingsService.addSettingToConfigFile(
                  settingProperties, SettingsService.DB, 2, providedDataBaseName.get());
            }
          }
          break;
        }
      case 2:
        {
          String dbsLocation =
              fileIOUtilities.getValueFromConfig(
                  SettingsService.DBS_BACKUPS_DIRECTORY, "=", settingProperties);
          List<Path> inputs =
              fileIOUtilities.listFiles(
                  Paths.get(StringUtils.isBlank(dbsLocation) ? "input/" : dbsLocation));

          String sqlDumpFile = ConsoleUtils.chooseDumpFile(console, inputs);

          if (sqlDumpFile == null) {
            break;
          }
          if (!alreadyLoadedDataBases.contains(sqlDumpFile.split(".")[0])) {
            dataBaseService.importDatabaseFile(sqlDumpFile, mySQLProps);
          }
          break;
        }

      default:
        {
          ConsoleUtils.showUnavailableOption(console);
          break;
        }
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
