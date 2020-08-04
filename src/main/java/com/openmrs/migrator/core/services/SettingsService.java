package com.openmrs.migrator.core.services;

import com.openmrs.migrator.core.exceptions.SettingsException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

public interface SettingsService {

  // settings resource file name
  String SETTINGS_PROPERTIES = "settings.properties";

  String PDI_RESOURCES_DIR = "pdiresources";

  String PDI_CONFIG = "config";

  // settings keys
  String DB_TEST_CONNECTION = "ETL_TEST_DATABASE_CONNECTION";

  String SOURCE_DB = "ETL_SOURCE_DATABASE";

  String MERGE_DB = "ETL_MERGE_DATABASE";

  String DB_HOST = "ETL_DATABASE_HOST";

  String DB_PORT = "ETL_DATABASE_PORT";

  String DB_USER = "ETL_DATABASE_USER";

  String DB_PASS = "ETL_DATABASE_PASSWORD";

  String DBS_BACKUPS_DIRECTORY = "EPTS_DATABASES_DIRECTORY";

  void fillConfigFile(Path target, Map<String, String> connDB) throws IOException;

  void addSettingToConfigFile(Path target, String labelName, int lineNumber, String configValue)
      throws IOException;

  void initializeKettleEnvironment() throws SettingsException;
}
