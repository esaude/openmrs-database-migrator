package com.openmrs.migrator.core.services.impl;

import com.ibatis.common.jdbc.ScriptRunner;
import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.model.DatabaseProps;
import com.openmrs.migrator.core.model.DatabaseProps.DbEngine;
import com.openmrs.migrator.core.services.DataBaseService;
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
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

/** Database operations */
@Service
public class DataBaseServiceImpl implements DataBaseService {

  @Override
  public void importDatabaseFile(String fileName, DatabaseProps databaseProps)
      throws SQLException, IOException {
    loadDatabase(new File(fileName), databaseProps);
  }

  @Override
  public List<String> oneColumnSQLSelectorCommand(
      DatabaseProps databaseProps, String sqlCommand, String column) throws SQLException {
    List<String> results = new ArrayList<>();
    ResultSet res = runExecuteSQL(databaseProps, sqlCommand);
    while (res.next()) {
      results.add(res.getString(column));
    }
    res.close();
    return results;
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
  public boolean testConnection(DatabaseProps databaseProps, boolean throwConnectionException)
      throws SettingsException {
    Boolean results = false;
    try {
      results = executeMySQLStatement(getConnection(databaseProps), "select 1");
    } catch (Exception e) {
      if (throwConnectionException) {
        throw new SettingsException(e);
      }
    }
    return results != null ? results : false;
  }

  private Connection getConnection(DatabaseProps databaseProps) throws SQLException {
    return DriverManager.getConnection(
        DbEngine.MYSQL.equals(databaseProps.getEngine())
            ? String.format(
                "jdbc:mysql://%s:%s/%s",
                databaseProps.getHost(),
                databaseProps.getPort(),
                StringUtils.isNotBlank(databaseProps.getDb()) ? databaseProps.getDb() : "")
            : databaseProps.getUrl(),
        databaseProps.getUsername(),
        databaseProps.getPassword());
  }

  private void loadDatabase(File dbPath, DatabaseProps databaseProps)
      throws SQLException, IOException {
    if (dbPath.exists()) {
      String db = databaseProps.getDb();
      databaseProps.setDb(null);
      runSQLUpdate(
          databaseProps,
          String.format(
              DbEngine.H2.equals(databaseProps.getEngine())
                  ? "CREATE SCHEMA %s"
                  : "CREATE DATABASE IF NOT EXISTS %s",
              db));
      databaseProps.setDb(db);
      ScriptRunner sr = new ScriptRunner(getConnection(databaseProps), false, false);
      Reader reader = new BufferedReader(new FileReader(dbPath));
      sr.runScript(reader);
    }
  }

  private void runSQLUpdate(DatabaseProps databaseProps, String sql) throws SQLException {
    Statement statement = getConnection(databaseProps).createStatement();
    statement.executeUpdate(sql);
    statement.close();
  }

  private ResultSet runExecuteSQL(DatabaseProps databaseProps, String sql) throws SQLException {
    Statement statement = getConnection(databaseProps).createStatement();
    return statement.executeQuery(sql);
  }
}
