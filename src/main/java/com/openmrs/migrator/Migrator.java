package com.openmrs.migrator;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.ConsoleUtils;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.Console;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
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
  public Optional<Void> call() throws IOException, InterruptedException {

    if (setup) {
      executeSetupCommand();
      settingsService.fillConfigFile();
    }

    if (run) {
      executeRunCommandLogic();
    }
    return Optional.empty();
  }

  private void runAllJobs() throws IOException {
    try {
      for (String t : jobs) {

        InputStream xml = fileIOUtilities.getResourceAsStream(t);
        pdiService.runJob(xml);
      }
    } catch (KettleException e) {
      // Do nothing kettle prints stack trace
    }
  }

  private void executeSetupCommand() throws IOException {
    List<String> pdiFiles = new ArrayList<>();

    pdiFiles.add("pdiresources/jobs/merge-patient-job.kjb");
    pdiFiles.add("pdiresources/transformations/merge-patient.ktr");
    pdiFiles.add("settings.properties");

    bootstrapService.createDirectoryStructure(dirList);
    bootstrapService.populateDefaultResources(pdiFiles);
  }

  private void executeRunCommandLogic() throws FileNotFoundException, IOException {

    String userConfigPassword = fileIOUtilities.getValueFromConfig(SettingsService.DB_PASS, "=");
    int choice = ConsoleUtils.startMigrationAproach(console);
    List<String> alreadyLoadedDataBases =
        dataBaseService.runSQLCommand(
            fileIOUtilities.getValueFromConfig(SettingsService.DB_USER, "="),
            userConfigPassword,
            "show databases");

    switch (choice) {
      case 1:
        Optional<String> providedDataBaseName = ConsoleUtils.getDatabaseDetaName(console);
        Optional<String> storedDataBaseName =
            fileIOUtilities.searchForDataBaseNameInSettingsFile(providedDataBaseName.get());

        if (!storedDataBaseName.isPresent()) {
          if (ConsoleUtils.isConnectionIsToBeStored(console)) {
            settingsService.addSettingToConfigFile(SettingsService.DB, providedDataBaseName.get());
          }
          fileIOUtilities.setConnectionToKettleFile(providedDataBaseName.get());
        }
        runAllJobs();
        break;

      case 2:
        String selectDBName =
            ConsoleUtils.getValidSelectedDataBase(
                console,
                dataBaseService.validateDataBaseNames(
                    fileIOUtilities.getAllDataBaseNamesFromConfigFile(), alreadyLoadedDataBases));
        if (selectDBName != null) {
          fileIOUtilities.setConnectionToKettleFile(selectDBName);
          runAllJobs();
        }

        break;
      case 3:
        String dbName =
            ConsoleUtils.getValidSelectedDataBase(console, new HashSet<>(alreadyLoadedDataBases));
        fileIOUtilities.setConnectionToKettleFile(dbName);
        runAllJobs();
        break;

      case 4:
        List<Path> inputs = fileIOUtilities.listFiles(Paths.get("input/"));

        String sqlDumpFile = ConsoleUtils.chooseDumpFile(console, inputs);

        if (sqlDumpFile == null) {
          break;
        }

        String databaseName = ConsoleUtils.getChosenDBName(console);

        dataBaseService.importDatabaseFile(databaseName, sqlDumpFile);
        fileIOUtilities.setConnectionToKettleFile(databaseName);
        runAllJobs();

        break;

      default:
        ConsoleUtils.showUnavailableOption(console);
        break;
    }
  }
}
