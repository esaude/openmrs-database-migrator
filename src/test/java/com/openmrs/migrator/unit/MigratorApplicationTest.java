package com.openmrs.migrator.unit;

import static org.junit.Assert.assertEquals;

import com.openmrs.migrator.MigratorApplication;
import com.openmrs.migrator.core.exceptions.SettingsException;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MigratorApplicationTest {

  private static final String MIGRATOR_HOME = "MIGRATOR_HOME";

  @Autowired private MigratorApplication migratorApplication;

  @Before
  public void setUp() {
    System.clearProperty(MIGRATOR_HOME);
  }

  @Test
  public void runShouldSetMigratorHomeVariableIfNotGiven() throws IOException, SettingsException {
    String userDir = System.getProperty("user.dir");
    migratorApplication.run();
    assertEquals(userDir, System.getProperty(MIGRATOR_HOME));
  }

  @Test
  public void runShouldNotSetMigratorHomeVariableIfGiven() throws IOException, SettingsException {
    String dir = "/opt/migrator";
    System.setProperty(MIGRATOR_HOME, dir);
    migratorApplication.run();
    assertEquals(dir, System.getProperty(MIGRATOR_HOME));
  }
}
