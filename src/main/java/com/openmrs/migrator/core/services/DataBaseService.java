package com.openmrs.migrator.core.services;

import java.io.IOException;
import java.util.List;

public interface DataBaseService {

  void importDatabaseFile(String databaseName, String fileName);

  void createDatabase(String databaseName);

  List<String> getDatabases(String password) throws IOException;
}
