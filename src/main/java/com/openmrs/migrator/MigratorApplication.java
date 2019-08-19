package com.openmrs.migrator;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class MigratorApplication implements CommandLineRunner {

  private static Logger LOG = LoggerFactory.getLogger(MigratorApplication.class);

  private final PDIService pdiService;

  private FileIOUtilities fileIOUtilities;

  private DataBaseService dataBaseService;

  private SettingsService settingsService;

  private BootstrapService bootstrapService;

  @Autowired
  public MigratorApplication(
      PDIService pdiService,
      FileIOUtilities fileIOUtilities,
      BootstrapService bootstrapService,
      DataBaseService dataBaseService,
      SettingsService settingsService) {
    this.pdiService = pdiService;
    this.fileIOUtilities = fileIOUtilities;
    this.bootstrapService = bootstrapService;
    this.dataBaseService = dataBaseService;
    this.settingsService = settingsService;
  }

  public static void main(String[] args) {
    SpringApplication.run(MigratorApplication.class, args);
  }

  // TODO: to be replaced with PICOCLI
  @Override
  public void run(String... args) {
    LOG.info("EXECUTING : command line runner");

    CommandLine.call(
        new Migrator(
            System.console(),
            pdiService,
            fileIOUtilities,
            bootstrapService,
            dataBaseService,
            settingsService),
        args);
  }
}
