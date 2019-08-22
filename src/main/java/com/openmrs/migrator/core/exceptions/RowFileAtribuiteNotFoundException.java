package com.openmrs.migrator.core.exceptions;

import java.io.IOException;

public class RowFileAtribuiteNotFoundException extends IOException {

  private static final long serialVersionUID = 1L;

  public RowFileAtribuiteNotFoundException(String message) {
    super(message);
  }

  public RowFileAtribuiteNotFoundException() {}
}
