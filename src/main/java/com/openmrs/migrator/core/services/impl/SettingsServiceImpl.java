package com.openmrs.migrator.core.services.impl;

import com.ibatis.common.jdbc.ScriptRunner;
import com.openmrs.migrator.core.services.SettingsService;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.util.EnvUtil;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

@Component
public class SettingsServiceImpl implements SettingsService {
  
  public void initializeKettleEnvironment(boolean testDbConnection) throws Exception {
    Properties props = new Properties();
    String settingsFile = SettingsService.PDI_RESOURCES_DIR + File.separator + SettingsService.KETTLE_PROPERTIES;
    InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(settingsFile);
    props.load(is);
    is.close();

    String host = props.getProperty(SettingsService.DB_HOST);
    String port = props.getProperty(SettingsService.DB_PORT);
    String db = props.getProperty(SettingsService.DB);
    String user = props.getProperty(SettingsService.DB_USER);
    String pass = props.getProperty(SettingsService.DB_PASS);
    String dbsLoaded = props.getProperty(SettingsService.DBS_ALREADY_LOADED);
    String dbsBackups = props.getProperty(SettingsService.DBS_BACKUPS);
    String dbsBackupsFolder = props.getProperty(SettingsService.DBS_BACKUPS_DIRECTORY);
    if(!testDbConnection || testConnection(host, port, db, user, pass)) {
      // load database backups
      File backupsFolder = new File(dbsBackupsFolder);
      if(backupsFolder.exists() && "false".equals(dbsLoaded) && StringUtils.isNotBlank(dbsBackups)) {
        loadEPTSDatabaseBackups(host, port, dbsBackups.split(","), backupsFolder, user, pass);
        // TODO fix these 2 lines below
        props.setProperty(SettingsService.DBS_ALREADY_LOADED, "true");
        props.store(new FileOutputStream(settingsFile), "MySQL backups loaded!");
      }

      // initialize kettle environment
      KettleEnvironment.init();

      // apply our props to default settings.properties
      EnvUtil.applyKettleProperties(props, true);
    }
  }

  private Connection getConnection(String host, String port, String database, String username, String password) throws Exception {
    return DriverManager.getConnection(String.format("jdbc:mysql://%s:%s/%s", host, port, database), username, password);
  }

  private boolean testConnection(String host, String port, String database, String username, String password) throws Exception {
    Boolean results = executeMySQLStatement(getConnection(host, port, database, username, password), "select 1");
    return results != null ? results : false;
  }

  private Boolean executeMySQLStatement(Connection conn, String statement) throws SQLException {
    Statement stmt = conn.createStatement();
    if (stmt == null) {
      return false;
    }
    ResultSet rs = stmt.executeQuery(statement);
    if (rs == null) {
      return false;
    }
    if (rs.next()) {
      return true;
    }
    rs.close();
    stmt.close();
    return null;
  }

  private void loadEPTSDatabaseBackups(String host, String port, String[] databases, File backupsFolder, String username, String password) throws Exception {
    for(String db: databases) {
      File dbPath = new File(backupsFolder.getAbsolutePath() + File.separator + db + ".sql");
      if(dbPath.exists()) {
        Statement statement = getConnection(host, port, "", username, password).createStatement();
        statement.executeUpdate(String.format("CREATE DATABASE IF NOT EXISTS %s", db));
        statement.close();
        ScriptRunner sr = new ScriptRunner(getConnection(host, port, db, username, password), false, false);
        Reader reader = new BufferedReader(new FileReader(dbPath));
        sr.runScript(reader);
      }
    }
  }
}
