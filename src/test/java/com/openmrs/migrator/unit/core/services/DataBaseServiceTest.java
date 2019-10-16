package com.openmrs.migrator.unit.core.services;

import com.openmrs.migrator.core.model.DatabaseProps;
import com.openmrs.migrator.core.model.DatabaseProps.DbEngine;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBaseServiceTest {

  @Autowired private DataBaseService dataBaseService;

  @Autowired private FileIOUtilities fileIOUtilities;

  private DatabaseProps databaseProps;

  private Connection dbConnection;

  @Before
  public void setUp() {
    databaseProps = new DatabaseProps("jdbc:h2:mem:openmrs", "sa", "sa");
    databaseProps.setEngine(DbEngine.H2);
    try {
      // initialise h2 connection
      Class.forName("org.h2.Driver");
      dbConnection =
          DriverManager.getConnection(
              databaseProps.getUrl(), databaseProps.getUsername(), databaseProps.getPassword());
    } catch (SQLException | ClassNotFoundException e) {
      Assert.assertNull(e);
    }
  }

  @Test
  public void tearDown() throws Exception {
    dbConnection.close();
  }

  @Test
  public void oneColumnSQLSelectorCommand() throws IOException, SQLException {
    List<String> alreadyLoadedDataBases =
        dataBaseService.oneColumnSQLSelectorCommand(databaseProps, "show databases", "SCHEMA_NAME");
    Assert.assertTrue(alreadyLoadedDataBases.size() > 0);
  }

  @Test
  public void testConnection() throws Exception {
    Assert.assertTrue(dataBaseService.testConnection(databaseProps, false));
  }

  @Test
  public void testConnectionWithException() throws Exception {
    Assert.assertTrue(dataBaseService.testConnection(databaseProps, true));
  }

  @Test
  public void importDatabaseFile() throws Exception {
    databaseProps.setDb("TESTS");
    List<String> loadedDataBases =
        dataBaseService.oneColumnSQLSelectorCommand(databaseProps, "show databases", "SCHEMA_NAME");
    String testsScript =
        DataBaseServiceTest.class.getClassLoader().getResource("tests.sql").getPath();
    dataBaseService.importDatabaseFile(testsScript, databaseProps);
    List<String> loadedDataBasesAfterwards =
        dataBaseService.oneColumnSQLSelectorCommand(databaseProps, "show databases", "SCHEMA_NAME");
    Assert.assertEquals(loadedDataBases.size(), loadedDataBasesAfterwards.size() - 1);
    Assert.assertTrue(loadedDataBasesAfterwards.contains("TESTS"));
    List<String> testCount =
        dataBaseService.oneColumnSQLSelectorCommand(
            databaseProps, "SELECT COUNT(*) FROM TEST", "COUNT(*)");
    Assert.assertEquals(testCount.size(), 1);
    Assert.assertTrue(testCount.contains("0"));
  }

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
              new DatabaseProps(host, port, user, pass, db), "show databases", "Database");
      Assert.assertFalse(names.isEmpty());
    }
  }
}
