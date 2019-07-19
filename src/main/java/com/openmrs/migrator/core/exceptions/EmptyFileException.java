package com.openmrs.migrator.core.exceptions;

/** This exception represents a problem when finding an empty file */
public class EmptyFileException extends Exception {

  private static final long serialVersionUID = 1L;

  public EmptyFileException() {
    super("File is empty");
  }
}
