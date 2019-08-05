package com.openmrs.migrator.core.exceptions;

public class SettingsException extends Exception {

  public SettingsException(Throwable e) {
    super("SettingsException: " + e.getMessage(), e);
  }
}
