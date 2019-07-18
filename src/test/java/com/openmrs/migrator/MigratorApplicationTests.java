package com.openmrs.migrator;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MigratorApplication.class)
public class MigratorApplicationTests {

  private static List<Path> structurePaths;

  @BeforeClass
  public static void initClass() {
    structurePaths =
        Arrays.asList(
            Paths.get("input"),
            Paths.get("output"),
            Paths.get("config"),
            Paths.get("input"),
            Paths.get("settings.properties"));
  }

  @Test
  public void executeSetupCommandSucessfully() throws IOException {

    MigratorApplication.main(new String[] {"setup"});

    structurePaths.forEach(path -> assertTrue(Files.exists(path)));
  }

  @Test
  public void executeRunCommandSucessfully() {

    MigratorApplication.main(new String[] {"run"});
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
