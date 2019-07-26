package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.config.ConfigurationStore;
import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Database operations */
@Service
public class DataBaseServiceImpl implements DataBaseService {

  private final CommandService commandService;

  private final ConfigurationStore configurationStore;

  @Autowired
  public DataBaseServiceImpl(CommandService commandService, ConfigurationStore configurationStore) {
    this.commandService = commandService;
    this.configurationStore = configurationStore;
  }

  @Override
  public void importDatabaseFile(String databaseName, String fileName) {
    commandService.runCommand(
        "mysql",
        "-u" + configurationStore.getDatabaseUser(),
        "-p" + configurationStore.getDatabasePassword(),
        "-h" + configurationStore.getDatabaseHost(),
        "-e",
        String.format("use %s; source %s;", databaseName, fileName));
  }

  @Override
  public void createDatabase(String databaseName) {
    commandService.runCommand(
        "mysql",
        "-u" + configurationStore.getDatabaseUser(),
        "-p" + configurationStore.getDatabasePassword(),
        "-h" + configurationStore.getDatabaseHost(),
        "-e",
        String.format(
            "drop database if exists %s; create database %s;", databaseName, databaseName));
  }
}
