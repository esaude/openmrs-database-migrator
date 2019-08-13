package com.openmrs.migrator.core.services;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.impl.MySQLProps;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

public interface DataBaseService {

  void importDatabaseFile(String fileName, MySQLProps mySQLProps) throws SQLException, IOException;

  List<String> oneColumnSQLSelectorCommand(MySQLProps mySQLProps, String sqlCommand, String column)
      throws IOException, SQLException;

  Set<String> validateDataBaseNames(List<String> fromConfig, List<String> fromMySql)
      throws FileNotFoundException, IOException;

  boolean testConnection(MySQLProps mySQLProps, boolean throwConnectionException)
      throws SettingsException;

  void loadDatabaseBackups(MySQLProps mySQLProps, String[] databases, File backupsFolder)
      throws SQLException, IOException;
}
