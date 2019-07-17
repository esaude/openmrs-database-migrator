package com.openmrs.migrator.core.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

public interface BootstrapService {

  int createDirectoryStructure(List<String> dirList, Path settingsProperties) throws IOException;

  Set<String> populateDefaultResource(Set<String> sourceFiles) throws IOException;
}
