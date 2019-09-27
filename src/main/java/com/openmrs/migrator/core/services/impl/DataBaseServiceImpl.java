package com.openmrs.migrator.core.services.impl;

import com.ibatis.common.jdbc.ScriptRunner;
import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.model.MySQLProps;
import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.DataBaseService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/** Database operations */
@Service
public class DataBaseServiceImpl implements DataBaseService {

  private final CommandService commandService;

  @Autowired
  public DataBaseServiceImpl(CommandService commandService) {
    this.commandService = commandService;
  }

  @Override
  public void importDatabaseFile(String fileName, MySQLProps mySQLProps)
      throws SQLException, IOException {
    loadDatabase(new File(fileName), mySQLProps);
  }

  @Override
  public List<String> oneColumnSQLSelectorCommand(
      MySQLProps mySQLProps, String sqlCommand, String column) throws IOException, SQLException {
    List<String> results = new ArrayList<>();
    ResultSet res = runExecuteSQL(mySQLProps, sqlCommand);
    while (res.next()) {
      results.add(res.getString(column));
    }
    res.close();
    return results;
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
  public boolean testConnection(MySQLProps mySQLProps, boolean throwConnectionException)
      throws SettingsException {
    Boolean results = false;
    try {
      results = executeMySQLStatement(getConnection(mySQLProps), "select 1");
    } catch (Exception e) {
      if (throwConnectionException) {
        throw new SettingsException(e);
      }
    }
    return results != null ? results : false;
  }

  private Connection getConnection(MySQLProps mySQLProps) throws SQLException {
    return DriverManager.getConnection(
        String.format(
            "jdbc:mysql://%s:%s/%s",
            mySQLProps.getHost(),
            mySQLProps.getPort(),
            StringUtils.isNotBlank(mySQLProps.getDb()) ? mySQLProps.getDb() : ""),
        mySQLProps.getUsername(),
        mySQLProps.getPassword());
  }

  @Override
  public void loadDatabaseBackups(MySQLProps mySQLProps, String[] databases, File backupsFolder)
      throws SQLException, IOException {
    for (String db : databases) {
      File dbPath = new File(backupsFolder.getAbsolutePath() + File.separator + db + ".sql");
      loadDatabase(dbPath, mySQLProps);
    }
  }

  private void loadDatabase(File dbPath, MySQLProps mySQLProps) throws SQLException, IOException {
    if (dbPath.exists()) {
      String db = mySQLProps.getDb();
      mySQLProps.setDb(null);
      runSQLUpdate(mySQLProps, String.format("CREATE DATABASE IF NOT EXISTS %s", db));
      mySQLProps.setDb(db);
      ScriptRunner sr = new ScriptRunner(getConnection(mySQLProps), false, false);
      Reader reader = new BufferedReader(new FileReader(dbPath));
      sr.runScript(reader);
    }
  }

  private void runSQLUpdate(MySQLProps mySQLProps, String sql) throws SQLException {
    Statement statement = getConnection(mySQLProps).createStatement();
    statement.executeUpdate(sql);
    statement.close();
  }

  private ResultSet runExecuteSQL(MySQLProps mySQLProps, String sql) throws SQLException {
    Statement statement = getConnection(mySQLProps).createStatement();
    statement.executeUpdate(sql);
    return statement.executeQuery(sql);
  }
}
