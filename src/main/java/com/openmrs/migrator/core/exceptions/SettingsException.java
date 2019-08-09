package com.openmrs.migrator.core.exceptions;

public class SettingsException extends Exception {

  private static final long serialVersionUID = -7692114236102622223L;

  public SettingsException(Throwable e) {
    super("SettingsException: " + e.getMessage(), e);
  }
}
