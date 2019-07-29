package com.openmrs.migrator.model;

public class DataBaseConnectionDetail {

  private String databaseName;

  private String username;

  private String password;

  public DataBaseConnectionDetail() {}

  public DataBaseConnectionDetail(String databaseName, String username, String password) {
    this.databaseName = databaseName;
    this.username = username;
    this.password = password;
  }

  public String getDataBaseName() {
    return databaseName;
  }

  public void setDataBaseName(String databaseName) {
    this.databaseName = databaseName;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
