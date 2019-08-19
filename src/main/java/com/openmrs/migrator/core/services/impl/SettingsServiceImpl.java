package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.util.EnvUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsServiceImpl implements SettingsService {

  private static Logger logger = LoggerFactory.getLogger(SettingsServiceImpl.class);

  @Autowired private FileIOUtilities fileIOUtilities;

  @Autowired private DataBaseService dataBaseService;

  public void fillConfigFile(Path target, Map<String, String> connDB) throws IOException {

    fileIOUtilities.writeToFile(
        target.toFile(),
        SettingsService.DB_USER + "=" + connDB.get(SettingsService.DB_USER),
        SettingsService.DB_PASS + "=" + connDB.get(SettingsService.DB_PASS),
        SettingsService.DB_HOST + "=" + connDB.get(SettingsService.DB_HOST),
        SettingsService.DB_PORT + "=" + connDB.get(SettingsService.DB_PORT));
  }

  public void addSettingToConfigFile(Path target, String labelName, String configVaule)
      throws IOException {
    logger.info("Adding " + labelName + ":" + configVaule + " in config file");

    List<String> lines = Files.readAllLines(target);
    lines.add(labelName + "=" + configVaule);
    Files.write(target, lines, StandardCharsets.UTF_8);
  }

  public void initializeKettleEnvironment() throws SettingsException {
    try {
      Properties props = new Properties();
      InputStream is = fileIOUtilities.getResourceAsStream(SettingsService.SETTINGS_PROPERTIES);
      props.load(is);
      is.close();

      String testConnection = props.getProperty(SettingsService.DB_TEST_CONNECTION);
      String host = props.getProperty(SettingsService.DB_HOST);
      String port = props.getProperty(SettingsService.DB_PORT);
      String db = props.getProperty(SettingsService.DB_SOURCE);
      String user = props.getProperty(SettingsService.DB_USER);
      String pass = props.getProperty(SettingsService.DB_PASS);
      String dbsLoaded = props.getProperty(SettingsService.DBS_ALREADY_LOADED);
      String dbsBackups = props.getProperty(SettingsService.DBS_BACKUPS);
      String dbsBackupsFolder = props.getProperty(SettingsService.DBS_BACKUPS_DIRECTORY);
      if ("false".equals(testConnection)
          || dataBaseService.testConnection(host, port, db, user, pass)) {
        // load database backups
        if (StringUtils.isNotBlank(dbsBackupsFolder)
            && StringUtils.isNotBlank(dbsBackups)
            && "false".equals(dbsLoaded)) {
          File backupsFolder = new File(dbsBackupsFolder);

          if (backupsFolder.exists()) {
            dataBaseService.loadDatabaseBackups(
                host, port, dbsBackups.split(","), backupsFolder, user, pass);
            // TODO fix these 2 lines below
            props.setProperty(SettingsService.DBS_ALREADY_LOADED, "true");
            props.store(
                new FileOutputStream(SettingsService.SETTINGS_PROPERTIES), "MySQL backups loaded!");
          }
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
