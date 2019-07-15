package com.openmrs.migrator.core.services;

import java.io.InputStream;
import org.pentaho.di.core.exception.KettleException;

public interface PDIService {

  /**
   * Runs a transformation from the filesystem
   *
   * <p>It returns false if the jobs fails
   *
   * @param xmlStream An input stream of the transformation file
   */
  boolean runJob(InputStream xmlStream) throws KettleException;
}
