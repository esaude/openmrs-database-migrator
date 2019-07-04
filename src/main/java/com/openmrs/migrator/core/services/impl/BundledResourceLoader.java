package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.ResourceLoader;
import java.io.InputStream;
import org.springframework.stereotype.Component;

/** Loads resources from the jar file */
@Component
public class BundledResourceLoader implements ResourceLoader {
  @Override
  public InputStream getResourceAsStream(String resource) {
    return getClass().getResourceAsStream("/" + resource);
  }
}
