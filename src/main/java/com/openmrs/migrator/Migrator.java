package com.openmrs.migrator;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.utilities.ConsoleUtils;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
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
import java.util.Set;
import java.util.concurrent.Callable;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(
    name = "migrator",
    subcommands = {RunCommand.class},
    mixinStandardHelpOptions = true)
public class Migrator implements Callable<Optional<Void>> {

  private PDIService pdiService;

  private FileIOUtilities fileIOUtilities;

  private BootstrapService bootstrapService;

  private DataBaseService dataBaseService;

  private CommandService commandService;

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
      PDIService pdiService,
      FileIOUtilities fileIOUtilities,
      BootstrapService bootstrapService,
      DataBaseService dataBaseService,
      CommandService commandService) {
    this.pdiService = pdiService;
    this.fileIOUtilities = fileIOUtilities;
    this.bootstrapService = bootstrapService;
    this.dataBaseService = dataBaseService;
    this.commandService = commandService;
  }

  @Override
  public Optional<Void> call() throws IOException, InterruptedException {

    if (setup) {
      executeSetupCommand();
      fileIOUtilities.fillConfigFile();
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
    int choice = ConsoleUtils.startMigrationAproach();

    switch (choice) {
      case 1:
        Optional<String> providedDataBaseName = ConsoleUtils.getDatabaseDetaName();
        Optional<String> storedDataBaseName =
            fileIOUtilities.searchForDataBaseNameInSettingsFile(providedDataBaseName.get());

        if (!storedDataBaseName.isPresent()) {
          if (ConsoleUtils.isConnectionIsToBeStored()) {
            fileIOUtilities.addSettingToConfigFile(providedDataBaseName.get());
          }
          fileIOUtilities.setConnectionToKettleFile(providedDataBaseName.get());
        }
        runAllJobs();
        break;

      case 2:
        String selectDBName =
            ConsoleUtils.getValidSelectedDataBase(
                validateDataBaseNames(fileIOUtilities.getValueFromConfig("password", "=")));
        if (selectDBName != null) {
          fileIOUtilities.setConnectionToKettleFile(selectDBName);
          runAllJobs();
        }

        break;
      case 3:
        String dbName =
            ConsoleUtils.getValidSelectedDataBase(
                new HashSet<>(
                    dataBaseService.getDatabases(
                        fileIOUtilities.getValueFromConfig("password", "="))));
        fileIOUtilities.setConnectionToKettleFile(dbName);
        runAllJobs();
        break;

      case 4:
        List<Path> inputs = fileIOUtilities.listFiles(Paths.get("input/"));

        String sqlDumpFile = ConsoleUtils.chooseDumpFile(inputs);

        if (sqlDumpFile == null) {
          break;
        }

        String databaseName = ConsoleUtils.getChosenDBName();

        dataBaseService.importDatabaseFile(databaseName, sqlDumpFile);
        fileIOUtilities.setConnectionToKettleFile(databaseName);
        runAllJobs();

        break;

      default:
        ConsoleUtils.showUnavailableOption();
        break;
    }
  }

  private Set<String> validateDataBaseNames(String password)
      throws FileNotFoundException, IOException {
    Set<String> validNames = new HashSet<>();
    List<String> fromConfig = fileIOUtilities.getAllDataBaseNamesFromConfigFile();

    List<String> fromMySql = dataBaseService.getDatabases(password);

    fromConfig.forEach(
        conf -> {
          fromMySql.forEach(
              mysql -> {
                if (conf.equals(mysql)) {
                  validNames.add(conf);
                }
              });
        });

    return validNames;
  }
}
