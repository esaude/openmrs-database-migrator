package com.openmrs.migrator.core.services.impl;

public class MySQLProps {
  private String host;
  private String port;
  private String username;
  private String password;
  private String db;

  public MySQLProps(String host, String port, String username, String password, String db) {
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.db = db;
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
}
