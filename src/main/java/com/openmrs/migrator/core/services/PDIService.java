package com.openmrs.migrator.core.services;

import java.io.InputStream;
import org.pentaho.di.core.exception.KettleException;

public interface PDIService {

  /**
   * Runs a transformation from the filesystem
   *
   * @param xmlStream An input stream of the transformation file
   */
  void runJob(InputStream xmlStream) throws KettleException;

  /** Merges OpenMRS databases */
  void mergeOpenMRS();
}
