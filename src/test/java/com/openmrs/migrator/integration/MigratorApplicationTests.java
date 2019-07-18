package com.openmrs.migrator.integration;

import static org.junit.Assert.*;

import com.openmrs.migrator.MigratorApplication;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MigratorApplication.class)
public class MigratorApplicationTests {

  private static List<Path> structurePaths;

  @Autowired private MigratorApplication migratorApplication;

  private CommandLineRunner commandLineRunner;

  @BeforeClass
  public static void initClass() {
    structurePaths =
        Arrays.asList(
            Paths.get("input"),
            Paths.get("output"),
            Paths.get("config"),
            Paths.get("pdiresources"),
            Paths.get("settings.properties"));
  }

  @Before
  public void init() {
    commandLineRunner = command -> migratorApplication.run(command);
  }

  @Test
  public void executeSetupCommandSucessfully() throws Exception {

    commandLineRunner.run("setup");

    structurePaths.forEach(path -> assertTrue(Files.exists(path)));
  }

  @Test
  public void executeRunCommandSucessfully() throws Exception {

    commandLineRunner.run("run");
  }

  @AfterClass
  public static void endUpClass() throws IOException {

    structurePaths.forEach(
        path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException e) {
            e.printStackTrace();
          }
        });
  }
}
