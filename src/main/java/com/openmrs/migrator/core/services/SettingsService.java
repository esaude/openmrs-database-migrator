package com.openmrs.migrator.core.services;

public interface SettingsService {
  // settings resource file name
  public static String KETTLE_PROPERTIES = "settings.properties";

  public static String PDI_RESOURCES_DIR = "pdiresources";

  // settings keys
  public static String DB = "ETL_SOURCE_DATABASE";

  public static String DB_HOST = "ETL_DATABASE_HOST";

  public static String DB_PORT = "ETL_DATABASE_PORT";

  public static String DB_USER = "ETL_DATABASE_USER";

  public static String DB_PASS = "ETL_DATABASE_PASSWORD";

  public static String DBS_ALREADY_LOADED = "EPTS_DATABASES_ALREADY_LOADED";

  public static String DBS_BACKUPS = "EPTS_DATABASES";

  public static String DBS_BACKUPS_DIRECTORY = "EPTS_DATABASES_DIRECTORY";

  public void initializeKettleEnvironment(boolean testDbConnection) throws Exception;
}
