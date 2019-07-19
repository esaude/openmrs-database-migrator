package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.util.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

@Component
public class SettingsServiceImpl implements SettingsService {
  @Autowired
  private FileIOUtilities fileIOUtilities;

  public void initializeKettleEnvironment(boolean testDbConnection) throws Exception {
    Properties props = new Properties();
    InputStream is = fileIOUtilities.getResourceAsStream(SettingsService.PDI_RESOURCES_DIR + File.separator + SettingsService.KETTLE_PROPERTIES);
    props.load(is);
    // close inPutStream if open
    if (is != null) {
      is.close();
    }
    if(!testDbConnection || testConnection(props.getProperty(SettingsService.DB_HOST), props.getProperty(SettingsService.DB_PORT), props.getProperty(SettingsService.DB), props.getProperty(SettingsService.DB_USER), props.getProperty(SettingsService.DB_PASS))) {
      // initialize kettle environment
      KettleEnvironment.init();

      // apply our props to default settings.properties
      EnvUtil.applyKettleProperties(props, true);
    }
  }

  private boolean testConnection(String host, String port, String database, String username, String password) throws Exception {
    ResultSet rs = null;
    Statement stmt = null;
    Connection conn = DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", host, port, database), username, password);
    stmt = conn.createStatement();
    if (stmt == null) {
      return false;
    }
    rs = stmt.executeQuery("select 1");
    if (rs == null) {
      return false;
    }
    if (rs.next()) {
      return true;
    }
    rs.close();
    stmt.close();
    return false;
  }
}
