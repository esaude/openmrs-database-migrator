package com.openmrs.migrator.unit.core.services;

import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.services.impl.MySQLProps;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBaseServiceTest {

  @Autowired private DataBaseService dataBaseService;

  @Autowired private FileIOUtilities fileIOUtilities;

  @Test
  public void getDatabasesNameShouldReturnAtLeastName() throws IOException, SQLException {
    Properties props = new Properties();
    InputStream is = fileIOUtilities.getResourceAsStream(SettingsService.SETTINGS_PROPERTIES);
    props.load(is);
    is.close();
    Assert.assertNotNull(props);
    String testConnection = props.getProperty(SettingsService.DB_TEST_CONNECTION);
    if ("true".equals(testConnection)) {
      String host = props.getProperty(SettingsService.DB_HOST);
      String port = props.getProperty(SettingsService.DB_PORT);
      String db = props.getProperty(SettingsService.SOURCE_DB);
      String user = props.getProperty(SettingsService.DB_USER);
      String pass = props.getProperty(SettingsService.DB_PASS);
      List<String> names =
          dataBaseService.oneColumnSQLSelectorCommand(
              new MySQLProps(host, port, user, pass, db), "show databases", "Database");
      Assert.assertFalse(names.isEmpty());
    }
  }

  @Test
  public void shouldFindOneValidDataBasefromConfigFile() throws FileNotFoundException, IOException {
    List<String> fromConfig = new ArrayList<>();
    List<String> fromMySql = new ArrayList<>();
    Set<String> validNames = dataBaseService.validateDataBaseNames(fromConfig, fromMySql);
    Assert.assertTrue(validNames.isEmpty());
  }
}