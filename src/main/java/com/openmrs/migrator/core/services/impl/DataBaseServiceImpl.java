package com.openmrs.migrator.core.services.impl;

import com.ibatis.common.jdbc.ScriptRunner;
import com.openmrs.migrator.core.config.ConfigurationStore;
import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.DataBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

  private Boolean executeMySQLStatement(Connection conn, String statement) throws SQLException {
    Statement stmt = conn.createStatement();
    if (stmt == null) {
      return false;
    }
    ResultSet rs = stmt.executeQuery(statement);
    if (rs == null) {
      return false;
    }
    if (rs.next()) {
      return true;
    }
    return null;
  }

  @Override
  public boolean testConnection(
      String host, String port, String database, String username, String password)
      throws SQLException {
    Boolean results =
        executeMySQLStatement(getConnection(host, port, database, username, password), "select 1");
    return results != null ? results : false;
  }

  private Connection getConnection(
      String host, String port, String database, String username, String password)
      throws SQLException {
    return DriverManager.getConnection(
        String.format("jdbc:mysql://%s:%s/%s", host, port, database), username, password);
  }

  @Override
  public void loadDatabaseBackups(
      String host,
      String port,
      String[] databases,
      File backupsFolder,
      String username,
      String password)
      throws SQLException, IOException {
    for (String db : databases) {
      File dbPath = new File(backupsFolder.getAbsolutePath() + File.separator + db + ".sql");
      if (dbPath.exists()) {
        Statement statement = getConnection(host, port, "", username, password).createStatement();
        statement.executeUpdate(String.format("CREATE DATABASE IF NOT EXISTS %s", db));
        statement.close();
        ScriptRunner sr =
            new ScriptRunner(getConnection(host, port, db, username, password), false, false);
        Reader reader = new BufferedReader(new FileReader(dbPath));
        sr.runScript(reader);
      }
    }
  }
}
