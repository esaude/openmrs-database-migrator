package com.openmrs.migrator.core.services;

public interface DataBaseService {

  void importDatabaseFile(String databaseName, String fileName);

  void createDatabase(String databaseName);
}
