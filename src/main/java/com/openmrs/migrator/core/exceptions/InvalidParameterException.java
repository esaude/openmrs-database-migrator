package com.openmrs.migrator.core.exceptions;

/** This exception represents a problem when finding an empty file */
public class InvalidParameterException extends Exception {

  private static final long serialVersionUID = 5955245630027306759L;

  public InvalidParameterException(Object parameter) {
    super("Invalid parameter: " + parameter);
  }
}
