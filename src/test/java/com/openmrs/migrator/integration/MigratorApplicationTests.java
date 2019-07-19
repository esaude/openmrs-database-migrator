package com.openmrs.migrator.integration;

import static org.junit.Assert.*;

import com.openmrs.migrator.MigratorApplication;
import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MigratorApplication.class)
public class MigratorApplicationTests {

  @Autowired private MigratorApplication migratorApplication;

  @Autowired private FileIOUtilities fileIOUtils;

  private CommandLineRunner commandLineRunner;

  private static List<String> structurePaths =
      Arrays.asList("input", "output", "config", "pdiresources", "settings.properties");;

  @Before
  public void init() throws IOException, InvalidParameterException {
    commandLineRunner = command -> migratorApplication.run(command);

    fileIOUtils.removeAllDirectories(structurePaths);
  }

  @Test
  public void executeSetupCommandSucessfully() throws Exception {

    commandLineRunner.run("setup");
    assertNotNull(commandLineRunner);

    structurePaths.forEach(path -> assertTrue(Files.exists(Paths.get(path))));
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
