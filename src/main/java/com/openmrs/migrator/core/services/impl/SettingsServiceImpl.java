package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

@Component
public class SettingsServiceImpl implements SettingsService {

  @Autowired
  private DataBaseService dataBaseService;

  @Autowired
  private FileIOUtilities fileIOUtilities;

  public void initializeKettleEnvironment(boolean testDbConnection) throws SettingsException {
    try {
      Properties props = new Properties();
      InputStream is = fileIOUtilities.getResourceAsStream(SettingsService.SETTINGS_PROPERTIES);
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
      if (!testDbConnection || dataBaseService.testConnection(host, port, db, user, pass)) {
        // load database backups
        File backupsFolder = new File(dbsBackupsFolder);
        if (backupsFolder.exists()
                && "false".equals(dbsLoaded)
                && StringUtils.isNotBlank(dbsBackups)) {
          dataBaseService.loadDatabaseBackups(host, port, dbsBackups.split(","), backupsFolder, user, pass);
          // TODO fix these 2 lines below
          props.setProperty(SettingsService.DBS_ALREADY_LOADED, "true");
          props.store(new FileOutputStream(SettingsService.SETTINGS_PROPERTIES), "MySQL backups loaded!");
        }

        // initialize kettle environment
        KettleEnvironment.init();

        // apply our props to default settings.properties
        EnvUtil.applyKettleProperties(props, true);
      }
    } catch (IOException | SQLException | KettleException e) {
      throw new SettingsException(e);
    }
  }
}
