package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BootstrapServiceImpl implements BootstrapService {

  private static Logger log = LoggerFactory.getLogger(BootstrapServiceImpl.class);

  private FileIOUtilities fileIOUtilities;

  @Autowired
  public BootstrapServiceImpl(FileIOUtilities fileIOUtils) {
    this.fileIOUtilities = fileIOUtils;
  }

  /**
   * Creates folders structure. If one of the folders exists, a warn log is raised informing the
   * folder won't be created because it already exists
   *
   * @return Stream of created files
   */
  @Override
  public void createDirectoryStructure(List<String> dirList) throws IOException {
    log.info("Creating folder structure");

    dirList.forEach(
        dir -> {
          fileIOUtilities.createDirectory(Paths.get(dir));
        });
  }

  /**
   * this method copies resources of the app to folder where the app is running
   *
   * @return Set of created files
   */
  @Override
  public void populateDefaultResources(List<String> sourceFiles) throws IOException {
    log.info("Populating PDI folders with default resources");

    sourceFiles.forEach(
        pdiFile -> {
          // Not the cleanest approach
          // TODO: we should write a wrapper class that will handle this for us
          // An option is to use a functional interface for this:
          // https://www.baeldung.com/java-lambda-exceptions
          try {
            fileIOUtilities.copyFileFromResources(pdiFile);
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
        });
  }
}
