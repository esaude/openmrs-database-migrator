package com.openmrs.migrator.core.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface BootstrapService {

  Stream<String> createDirectoryStructure(List<String> dirList, Path settingsProperties)
      throws IOException;

  Set<String> populateDefaultResource(Set<String> sourceFiles) throws IOException;
}
