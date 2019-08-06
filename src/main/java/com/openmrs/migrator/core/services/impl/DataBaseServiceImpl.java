package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.config.ConfigurationStore;
import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.DataBaseService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Database operations */
@Service
public class DataBaseServiceImpl implements DataBaseService {

  private final CommandService commandService;

  private final ConfigurationStore configurationStore;

  private final FileIOUtilities fileIOUtilities;

  @Autowired
  public DataBaseServiceImpl(
      CommandService commandService,
      ConfigurationStore configurationStore,
      FileIOUtilities fileIOUtilities) {
    this.commandService = commandService;
    this.configurationStore = configurationStore;
    this.fileIOUtilities = fileIOUtilities;
  }

  @Override
  public void importDatabaseFile(String databaseName, String fileName) throws IOException {
    commandService.runCommand(
        "mysql",
        "-u" + fileIOUtilities.getValueFromConfig(SettingsService.DB_USER, "="),
        "-p" + fileIOUtilities.getValueFromConfig(SettingsService.DB_PASS, "="),
        "-h" + fileIOUtilities.getValueFromConfig(SettingsService.DB_HOST, "="),
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

  @Override
  public List<String> runSQLCommand(String username, String password, String sqlCommand)
      throws IOException {
    Process p =
        Runtime.getRuntime()
            .exec(new String[] {"mysql", "-u", username, "-p" + password, "-e", sqlCommand});

    String result =
        new BufferedReader(new InputStreamReader(p.getInputStream()))
            .lines()
            .collect(Collectors.joining(","));

    return Arrays.asList(result.split(","));
  }

  @Override
  public Set<String> validateDataBaseNames(List<String> fromConfig, List<String> fromMySql)
      throws FileNotFoundException, IOException {
    Set<String> validNames = new HashSet<>();

    fromConfig.forEach(
        conf -> {
          fromMySql.forEach(
              mysql -> {
                if (conf.equals(mysql)) {
                  validNames.add(conf);
                }
              });
        });

    return validNames;
  }
}
