package com.openmrs.migrator.core.services;

import java.io.InputStream;

public interface PDIService {

  /**
   * Runs a transformation from the filesystem
   *
   * @param xmlStream An input stream of the transformation file
   */
  void runTransformation(InputStream xmlStream);
}
