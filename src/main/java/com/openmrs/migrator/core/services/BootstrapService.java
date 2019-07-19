package com.openmrs.migrator.core.services;

import java.io.IOException;
import java.util.List;

public interface BootstrapService {

  void createDirectoryStructure(List<String> dirList) throws IOException;

  void populateDefaultResources(List<String> sourceFiles) throws IOException;
}
