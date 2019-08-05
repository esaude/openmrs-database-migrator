package com.openmrs.migrator.core.services;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public interface DataBaseService {

  void importDatabaseFile(String databaseName, String fileName);

  void createDatabase(String databaseName);

  boolean testConnection(
          String host, String port, String database, String username, String password)
          throws SQLException;

  void loadDatabaseBackups(
          String host,
          String port,
          String[] databases,
          File backupsFolder,
          String username,
          String password)
          throws SQLException, IOException;
}
