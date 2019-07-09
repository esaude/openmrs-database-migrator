package com.openmrs.migrator.core.config;

import org.springframework.stereotype.Component;

/** Access to all system configuration */
@Component
public class ConfigurationStore {

  public String getDatabaseHost() {
    return "localhost";
  }

  public String getDatabasePassword() {
    return "Admin123";
  }

  public String getDatabaseUser() {
    return "etl";
  }
}
