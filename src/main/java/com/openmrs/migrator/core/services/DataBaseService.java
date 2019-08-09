package com.openmrs.migrator.core.services;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface DataBaseService {

  void importDatabaseFile(String databaseName, String fileName) throws IOException;

  void createDatabase(String databaseName);

  List<String> runSQLCommand(String username, String password, String sqlCommand)
      throws IOException;

  Set<String> validateDataBaseNames(List<String> fromConfig, List<String> fromMySql)
      throws FileNotFoundException, IOException;

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
