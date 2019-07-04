package com.openmrs.migrator.core.services;

import java.io.InputStream;

public interface ResourceLoader {
  /**
   * @param resource The resource file name
   * @return An input stream for the resource file
   */
  InputStream getResourceAsStream(String resource);
}
