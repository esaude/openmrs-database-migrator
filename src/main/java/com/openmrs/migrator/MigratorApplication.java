package com.openmrs.migrator;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigratorApplication implements CommandLineRunner {

  private static Logger LOG = LoggerFactory.getLogger(MigratorApplication.class);

  private final PDIService pdiService;

  private FileIOUtilities fileIOUtilities;

  private String[] jobs = {SettingsService.PDI_RESOURCES_DIR + "/jobs/merge-patient-job.kjb"};

  private List<String> dirList =
      Arrays.asList(
          "input/",
          "output/",
          "config/",
          "pdiresources/",
          "pdiresources/transformations/",
          "pdiresources/jobs/",
          "plugins/");

  private BootstrapService bootstrapService;

  @Autowired
  public MigratorApplication(
      PDIService pdiService, FileIOUtilities fileIOUtilities, BootstrapService bootstrapService) {
    this.pdiService = pdiService;
    this.fileIOUtilities = fileIOUtilities;
    this.bootstrapService = bootstrapService;
  }

  public static void main(String[] args) {
    SpringApplication.run(MigratorApplication.class, args);
  }

  // TODO: to be replaced with PICOCLI
  @Override
  public void run(String... args) throws SettingsException, IOException {
    LOG.info("EXECUTING : command line runner");

    for (int i = 0; i < args.length; ++i) {
      LOG.info("args[{}]: {}", i, args[i]);
    }

    if (args.length > 0 && "setup".equals(args[0])) {

      executeSetupCommand();
    }

    if (args.length > 0 && "run".equals(args[0])) {
      runAllJobs();
    } else {
      System.out.println("Usage: migrator run");
    }
  }

  private void runAllJobs() throws SettingsException, IOException {
    for (String t : jobs) {
      InputStream xml = fileIOUtilities.getResourceAsStream(t);
      pdiService.runJob(xml);
    }
  }

  private void executeSetupCommand() throws IOException {
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
    pdiFiles.add("settings.properties");
    pdiFiles.add("plugins/pdi-core-plugins-impl-8.2.0.7-719.jar");

    bootstrapService.createDirectoryStructure(dirList);
    bootstrapService.populateDefaultResources(pdiFiles);
  }
}
