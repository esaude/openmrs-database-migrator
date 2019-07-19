package com.openmrs.migrator.core.services;

import java.io.InputStream;

public interface PDIService {

  /**
   * Runs a transformation from the filesystem
   *
   * <p>It returns false if the jobs fails
   *
   * @param xmlStream An input stream of the transformation file
   */
  boolean runJob(InputStream xmlStream) throws Exception;
}
