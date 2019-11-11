package com.openmrs.migrator.core.services;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.model.DatabaseProps;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public interface DataBaseService {

  void importDatabaseFile(String fileName, DatabaseProps databaseProps)
      throws SQLException, IOException;

  List<String> oneColumnSQLSelectorCommand(
      DatabaseProps databaseProps, String sqlCommand, String column)
      throws IOException, SQLException;

  boolean testConnection(DatabaseProps databaseProps, boolean throwConnectionException)
      throws SettingsException;
}
