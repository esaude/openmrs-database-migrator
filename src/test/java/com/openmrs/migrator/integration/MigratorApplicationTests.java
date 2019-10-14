package com.openmrs.migrator.integration;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.openmrs.migrator.MigratorApplication;
import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import picocli.CommandLine.ExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MigratorApplication.class)
public class MigratorApplicationTests {

  @Autowired private FileIOUtilities fileIOUtils;

  @Autowired private MigratorApplication migratorApplication;

  private String pwd;

  private List<String> structurePaths =
      Arrays.asList(
          "input",
          "output",
          SettingsService.PDI_CONFIG,
          SettingsService.PDI_RESOURCES_DIR,
          SettingsService.SETTINGS_PROPERTIES);

  @Before
  public void init() {
    assertNotNull(migratorApplication);
    pwd = System.getProperty("user.dir");
    notSetup();
  }

  @Test
  public void executeWrongCommand() {
    migratorApplication.run("wrongCommand");
    notSetup();
  }

  private void notSetup() {
    assertFalse(new File(pwd + File.separator + "input").exists());
    assertFalse(new File(pwd + File.separator + "output").exists());
    assertFalse(new File(pwd + File.separator + SettingsService.PDI_CONFIG).exists());
    assertFalse(new File(pwd + File.separator + SettingsService.SETTINGS_PROPERTIES).exists());
    assertFalse(new File(pwd + File.separator + SettingsService.PDI_RESOURCES_DIR).exists());
  }

  @Test
  public void executeSetupCommand() {
    migratorApplication.run("setup");
    for (String n : structurePaths) {
      boolean exists = false;
      for (String f : new File(pwd).list()) {
        if (f.equals(n)) {
          exists = true;
        }
      }
      assertTrue(exists);
    }
  }

  @Test(expected = ExecutionException.class)
  public void executeRunCommandBeforeSetup() throws ExecutionException {
    migratorApplication.run("run");
  }

  @After
  public void cleanUp() throws IOException, InvalidParameterException {
    fileIOUtils.removeAllDirectories(structurePaths);
  }
}
