package com.openmrs.migrator.core.services;

public interface DBService {

  void importDatabaseFile(String databaseName, String fileName);

  void createDatabase(String databaseName);
}
