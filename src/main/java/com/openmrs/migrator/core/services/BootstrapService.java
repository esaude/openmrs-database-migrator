package com.openmrs.migrator.core.services;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public interface BootstrapService {

  boolean createDirectoryStructure(List<String> dirList) throws IOException;

  boolean populateDefaultResources(Map<String, InputStream> sourceFiles) throws IOException;
}
