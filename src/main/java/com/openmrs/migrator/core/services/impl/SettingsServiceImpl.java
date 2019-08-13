package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
        SettingsService.DB_TEST_CONNECTION + "=" + connDB.get(SettingsService.DB_TEST_CONNECTION),
        SettingsService.DB_USER + "=" + connDB.get(SettingsService.DB_USER),
        SettingsService.DB_PASS + "=" + connDB.get(SettingsService.DB_PASS),
        SettingsService.DB_HOST + "=" + connDB.get(SettingsService.DB_HOST),
        SettingsService.DB_PORT + "=" + connDB.get(SettingsService.DB_PORT),
        SettingsService.DBS_BACKUPS_DIRECTORY
            + "="
            + connDB.get(SettingsService.DBS_BACKUPS_DIRECTORY));
  }

  public void addSettingToConfigFile(
      Path target, String labelName, int lineNumber, String configValue) throws IOException {
    logger.info("Adding " + labelName + ":" + configValue + " in config file");

    List<String> lines = Files.readAllLines(target);
    for (String line : lines) {
      if (line.startsWith(labelName + "=")) {
        lines.remove(line);
        break;
      }
    }
    lines.add(lineNumber - 1, labelName + "=" + configValue);
    Files.write(target, lines, StandardCharsets.UTF_8);
  }

  public void initializeKettleEnvironment() throws SettingsException {
    try {
      Properties props = new Properties();
      File settingsFile = new File(SettingsService.SETTINGS_PROPERTIES);
      InputStream is =
          settingsFile.exists()
              ? new FileInputStream(settingsFile)
              : fileIOUtilities.getResourceAsStream(SettingsService.SETTINGS_PROPERTIES);
      props.load(is);
      is.close();

      String testConnection = props.getProperty(SettingsService.DB_TEST_CONNECTION);
      String host = props.getProperty(SettingsService.DB_HOST);
      String port = props.getProperty(SettingsService.DB_PORT);
      String db = props.getProperty(SettingsService.SOURCE_DB);
      String user = props.getProperty(SettingsService.DB_USER);
      String pass = props.getProperty(SettingsService.DB_PASS);
      MySQLProps mysqlOpts = new MySQLProps(host, port, user, pass, db);
      if ("false".equals(testConnection) || dataBaseService.testConnection(mysqlOpts, true)) {
        // initialize kettle environment
        KettleEnvironment.init();

        // apply our props from default settings.properties
        EnvUtil.applyKettleProperties(props, true);
      }
    } catch (IOException | SettingsException | KettleException e) {
      throw new SettingsException(e);
    }
  }
}
