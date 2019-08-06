package com.openmrs.migrator.core.services;

import java.io.IOException;

public interface SettingsService {

  // settings resource file name
  String SETTINGS_PROPERTIES = "settings.properties";

  String PDI_RESOURCES_DIR = "pdiresources";

  // settings keys
  String DB_TEST_CONNECTION = "ETL_TEST_DATABASE_CONNECTION";

  String DB = "ETL_SOURCE_DATABASE";

  String DB_HOST = "ETL_DATABASE_HOST";

  String DB_PORT = "ETL_DATABASE_PORT";

  String DB_USER = "ETL_DATABASE_USER";

  String DB_PASS = "ETL_DATABASE_PASSWORD";

  String DBS_ALREADY_LOADED = "EPTS_DATABASES_ALREADY_LOADED";

  String DBS_BACKUPS = "EPTS_DATABASES";

  String DBS_BACKUPS_DIRECTORY = "EPTS_DATABASES_DIRECTORY";

  void fillConfigFile() throws IOException;

  void addSettingToConfigFile(String labelName, String configVaule) throws IOException;
}
