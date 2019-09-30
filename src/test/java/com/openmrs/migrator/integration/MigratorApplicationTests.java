package com.openmrs.migrator.integration;

import static org.junit.Assert.*;

import com.openmrs.migrator.Migrator;
import com.openmrs.migrator.MigratorApplication;
import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import picocli.CommandLine;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MigratorApplication.class)
public class MigratorApplicationTests {

  private Migrator migrator;

  @Autowired private FileIOUtilities fileIOUtils;

  @Autowired private PDIService pdiService;

  @Autowired private BootstrapService bootstrapService;

  @Autowired private DataBaseService dataBaseService;

  @Autowired private SettingsService settingsService;

  private CommandLineRunner commandLineRunner;

  private static List<String> structurePaths =
      Arrays.asList(
          "input",
          "output",
          "config",
          "pdiresources",
          SettingsService.PDI_PLUGINS_DIR,
          SettingsService.SETTINGS_PROPERTIES);

  @Before
  public void init() throws IOException, InvalidParameterException {

    migrator =
        new Migrator(
            System.console(),
            pdiService,
            fileIOUtils,
            bootstrapService,
            dataBaseService,
            settingsService);
    commandLineRunner = command -> migrator.call();

    fileIOUtils.removeAllDirectories(structurePaths);
  }

  @Test
  public void executeSetupCommand() throws Exception {

    CommandLine.call(migrator, "setup");
    assertNotNull(commandLineRunner);
  }

  @Test
  public void executeRunCommandSucessfully() throws Exception {

    commandLineRunner.run("run");
    assertNotNull(commandLineRunner);
  }

  @After
  public void cleanUp() throws IOException, InvalidParameterException {

    fileIOUtils.removeAllDirectories(structurePaths);
  }
}
