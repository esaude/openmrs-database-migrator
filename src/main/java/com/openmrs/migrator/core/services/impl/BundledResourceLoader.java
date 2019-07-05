package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.stereotype.Component;

/** Loads resources from the jar file */
@Component
public class BundledResourceLoader implements ResourceLoader {
  @Override
  public InputStream getResourceAsStream(String resource) throws IOException {
    InputStream resourceAsStream = getClass().getResourceAsStream("/" + resource);
    if (resourceAsStream == null) {
      throw new IOException("Could not load resource " + resource);
    }
    return resourceAsStream;
  }
}
