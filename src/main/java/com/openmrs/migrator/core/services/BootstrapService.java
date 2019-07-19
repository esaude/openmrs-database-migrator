package com.openmrs.migrator.core.services;

import java.io.IOException;
import java.util.List;

public interface BootstrapService {

  boolean createDirectoryStructure(List<String> dirList) throws IOException;

  boolean populateDefaultResources(List<String> sourceFiles) throws IOException;
}
