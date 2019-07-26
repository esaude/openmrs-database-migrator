package com.openmrs.migrator.model;

public class DataBaseDetail {

  private String name;

  private String username;

  private String password;

  public DataBaseDetail() {}

  public DataBaseDetail(String name, String username, String password) {}

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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
