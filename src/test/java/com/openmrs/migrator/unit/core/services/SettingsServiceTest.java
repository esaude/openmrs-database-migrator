package com.openmrs.migrator.unit.core.services;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.SettingsService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.core.util.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SettingsServiceTest {

  @Autowired private SettingsService settingsService;

  private Path tempSettingsFile;

  @Before
  public void setUp() {
    tempSettingsFile =
        Paths.get(
            System.getProperty("java.io.tmpdir")
                + File.separator
                + SettingsService.SETTINGS_PROPERTIES);
  }

  @After
  public void cleanUp() throws IOException {
    Files.delete(tempSettingsFile);
  }

  @Ignore
  public void initializeKettleEnvironment() throws SettingsException {
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_TEST_CONNECTION));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.SOURCE_DB));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_HOST));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_PORT));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_USER));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_PASS));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DBS_BACKUPS_DIRECTORY));

    settingsService.initializeKettleEnvironment();

    Assert.assertEquals("false", EnvUtil.getSystemProperty(SettingsService.DB_TEST_CONNECTION));
    Assert.assertEquals("fgh", EnvUtil.getSystemProperty(SettingsService.SOURCE_DB));
    Assert.assertEquals("127.0.0.1", EnvUtil.getSystemProperty(SettingsService.DB_HOST));
    Assert.assertEquals("3306", EnvUtil.getSystemProperty(SettingsService.DB_PORT));
    Assert.assertEquals("root", EnvUtil.getSystemProperty(SettingsService.DB_USER));
    Assert.assertEquals("codecode", EnvUtil.getSystemProperty(SettingsService.DB_PASS));
    Assert.assertEquals(
        "./input", EnvUtil.getSystemProperty(SettingsService.DBS_BACKUPS_DIRECTORY));
  }

  @Test
  public void addSettingToConfigFile() throws Exception {
    String source =
        SettingsServiceTest.class
            .getClassLoader()
            .getResource(SettingsService.SETTINGS_PROPERTIES)
            .getPath();
    Files.copy(Paths.get(source), tempSettingsFile);
    settingsService.addSettingToConfigFile(
        tempSettingsFile, "TEST_SETTING", 7, "nothing_important_really");
    List<String> lines = Files.readAllLines(tempSettingsFile);
    Assert.assertEquals("TEST_SETTING=nothing_important_really", lines.get(6));
    Assert.assertEquals("EPTS_DATABASES_DIRECTORY=./input", lines.get(7));
  }

  @Test
  public void addSettingToConfigFileShouldOverwriteProperty() throws Exception {
    String source =
        SettingsServiceTest.class
            .getClassLoader()
            .getResource(SettingsService.SETTINGS_PROPERTIES)
            .getPath();
    Files.copy(Paths.get(source), tempSettingsFile);
    settingsService.addSettingToConfigFile(tempSettingsFile, "EPTS_DATABASES_DIRECTORY", 7, "~/in");
    List<String> lines = Files.readAllLines(tempSettingsFile);
    Assert.assertEquals("EPTS_DATABASES_DIRECTORY=~/in", lines.get(6));
  }

  @Test
  public void fillConfigFile() throws Exception {
    Map<String, String> connDB = new HashMap<>();
    connDB.put(SettingsService.DB_TEST_CONNECTION, "false");
    connDB.put(SettingsService.DB_USER, "user");
    connDB.put(SettingsService.DB_PASS, "pass");
    connDB.put(SettingsService.DB_HOST, "localhost");
    connDB.put(SettingsService.DB_PORT, "3306");
    connDB.put(SettingsService.DBS_BACKUPS_DIRECTORY, "./input");
    settingsService.fillConfigFile(tempSettingsFile, connDB);
    List<String> lines = Files.readAllLines(tempSettingsFile);
    Assert.assertEquals(SettingsService.DB_TEST_CONNECTION + "=false", lines.get(0));
    Assert.assertEquals(SettingsService.DB_USER + "=user", lines.get(1));
    Assert.assertEquals(SettingsService.DB_PASS + "=pass", lines.get(2));
    Assert.assertEquals(SettingsService.DB_HOST + "=localhost", lines.get(3));
    Assert.assertEquals(SettingsService.DB_PORT + "=3306", lines.get(4));
    Assert.assertEquals(SettingsService.DBS_BACKUPS_DIRECTORY + "=./input", lines.get(5));
  }

  private void emptyTempValuesOfConfigFile() throws IOException {
    settingsService.fillConfigFile(tempSettingsFile, new HashMap<>());
    Assert.assertTrue(tempSettingsFile.toFile().exists());
    List<String> lines = Files.readAllLines(tempSettingsFile);
    Assert.assertEquals(SettingsService.DB_TEST_CONNECTION + "=null", lines.get(0));
    Assert.assertEquals(SettingsService.DB_USER + "=null", lines.get(1));
    Assert.assertEquals(SettingsService.DB_PASS + "=null", lines.get(2));
    Assert.assertEquals(SettingsService.DB_HOST + "=null", lines.get(3));
    Assert.assertEquals(SettingsService.DB_PORT + "=null", lines.get(4));
    Assert.assertEquals(SettingsService.DBS_BACKUPS_DIRECTORY + "=null", lines.get(5));
  }

  @Test
  public void fillConfigFileWithEmptyValues() throws IOException {
    Assert.assertFalse(tempSettingsFile.toFile().exists());
    emptyTempValuesOfConfigFile();
  }

  @Test(expected = SettingsException.class)
  public void initializeKettleEnvironmentWithEmptyProperties()
      throws IOException, SettingsException {
    Assert.assertFalse(tempSettingsFile.toFile().exists());
    emptyTempValuesOfConfigFile();
    Path source =
        Paths.get(
            System.getProperty("user.dir") + File.separator + SettingsService.SETTINGS_PROPERTIES);
    if (source.toFile().exists()) {
      Files.delete(source);
    }
    Files.copy(tempSettingsFile, source);
    settingsService.initializeKettleEnvironment();
  }
}
