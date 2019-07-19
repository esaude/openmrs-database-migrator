package com.openmrs.migrator;

import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import org.pentaho.di.core.exception.KettleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.InputStream;

@SpringBootApplication
public class MigratorApplication implements CommandLineRunner {

  private static Logger LOG = LoggerFactory.getLogger(MigratorApplication.class);

  private final PDIService pdiService;

  private FileIOUtilities fileIOUtilities;

  private String[] jobs = {SettingsService.PDI_RESOURCES_DIR + "/jobs/merge-patient-job.kjb"};

  @Autowired
  public MigratorApplication(PDIService pdiService, FileIOUtilities fileIOUtilities) {
    this.pdiService = pdiService;
    this.fileIOUtilities = fileIOUtilities;
  }

  public static void main(String[] args) {
    SpringApplication.run(MigratorApplication.class, args);
  }

  @Override
  public void run(String... args) throws Exception {
    LOG.info("EXECUTING : command line runner");

    for (int i = 0; i < args.length; ++i) {
      LOG.info("args[{}]: {}", i, args[i]);
    }

    if (args.length > 0 && "run".equals(args[0])) {
      runAllJobs();
    } else {
      System.out.println("Usage: migrator run");
    }
  }

  private void runAllJobs() throws Exception {
    try {
      for (String t : jobs) {

        InputStream xml = fileIOUtilities.getResourceAsStream(t);
        pdiService.runJob(xml);
      }
    } catch (KettleException e) {
      // Do nothing kettle prints stack trace
    }
  }
}
