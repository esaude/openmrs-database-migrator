package com.openmrs.migrator.core.services;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import org.junit.Assert;
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

  @Test(expected = CommunicationsException.class)
  public void initializeKettleEnvironmentShouldThrowExceptionWithNonExistingConnection()
      throws Exception {
    settingsService.initializeKettleEnvironment(true);
  }

  @Test
  public void initializeKettleEnvironment() throws Exception {
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_HOST));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_PORT));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_USER));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DB_PASS));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DBS_ALREADY_LOADED));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DBS_BACKUPS));
    Assert.assertNull(EnvUtil.getSystemProperty(SettingsService.DBS_BACKUPS_DIRECTORY));

    settingsService.initializeKettleEnvironment(false);

    Assert.assertEquals("fgh", EnvUtil.getSystemProperty(SettingsService.DB));
    Assert.assertEquals("127.0.0.1", EnvUtil.getSystemProperty(SettingsService.DB_HOST));
    Assert.assertEquals("3306", EnvUtil.getSystemProperty(SettingsService.DB_PORT));
    Assert.assertEquals("root", EnvUtil.getSystemProperty(SettingsService.DB_USER));
    Assert.assertEquals("codecode", EnvUtil.getSystemProperty(SettingsService.DB_PASS));
    Assert.assertEquals("false", EnvUtil.getSystemProperty(SettingsService.DBS_ALREADY_LOADED));
    Assert.assertEquals("", EnvUtil.getSystemProperty(SettingsService.DBS_BACKUPS));
    Assert.assertEquals(
        "./input", EnvUtil.getSystemProperty(SettingsService.DBS_BACKUPS_DIRECTORY));
  }
}