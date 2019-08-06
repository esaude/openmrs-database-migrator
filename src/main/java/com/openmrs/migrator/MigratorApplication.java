package com.openmrs.migrator;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.Console;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ExitCodeGenerator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import picocli.CommandLine;

@SpringBootApplication
public class MigratorApplication implements CommandLineRunner, ExitCodeGenerator {

  private int exitCode;

  @Autowired private PDIService pdiService;

  @Autowired private FileIOUtilities fileIOUtilities;

  @Autowired private BootstrapService bootstrapService;

  @Autowired private DataBaseService dataBaseService;

  @Autowired private SettingsService settingsService;

  private Console console = System.console();

  @Override
  public void run(String... args) throws Exception {
    CommandLine.call(
        new Migrator(
            console,
            pdiService,
            fileIOUtilities,
            bootstrapService,
            dataBaseService,
            settingsService),
        args);
  }

  @Override
  public int getExitCode() {
    return exitCode;
  }

  public static void main(String[] args) {
    // let Spring instantiate and inject dependencies
    SpringApplication.exit(SpringApplication.run(MigratorApplication.class, args));
  }
}
