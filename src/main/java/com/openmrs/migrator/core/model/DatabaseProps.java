package com.openmrs.migrator.core.model;

public class DatabaseProps {
  private String host;
  private String port;
  private String username;
  private String password;
  private String db;
  // use for other drivers besides mysql
  private String url;
  private DbEngine engine = DbEngine.MYSQL;

  public DatabaseProps(String host, String port, String username, String password, String db) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.db = db;
  }

  public enum DbEngine {
    MYSQL,
    H2
  }

  public DatabaseProps(String url, String username, String password) {
    this.username = username;
    this.password = password;
    this.url = url;
  }

  public String getHost() {
    return host;
  }

  public String getPort() {
    return port;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getDb() {
    return db;
  }

  public void setDb(String db) {
    this.db = db;
  }

  public String getUrl() {
    return url;
  }

  public DbEngine getEngine() {
    return engine;
  }

  public void setEngine(DbEngine engine) {
    this.engine = engine;
  }
}
