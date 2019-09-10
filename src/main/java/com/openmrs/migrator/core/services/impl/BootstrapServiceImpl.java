package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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
   * @return boolean indicating success or failure
   */
  @Override
  public boolean createDirectoryStructure(List<String> dirList) throws IOException {
    log.info("Creating folder structure");

    for (String dir : dirList) {
      // Not the cleanest approach
      // TODO: we should write a wrapper class that will handle this for us
      // An option is to use a functional interface for this:
      // https://www.baeldung.com/java-lambda-exceptions
      try {
        fileIOUtilities.createDirectory(Paths.get(dir));
      } catch (IOException ex) {
        log.error("An IO exception occurred while creating directories", ex);
        return false;
      }
    }

    return true;
  }

  /**
   * this method copies resources of the app to folder where the app is running
   *
   * @return boolean indicating success or failure
   */
  @Override
  public boolean populateDefaultResources(Map<String, InputStream> sourceFiles, String targetFolder)
      throws IOException {
    log.info("Populating PDI folders with default resources");
    FileOutputStream outStream;
    for (String pdiFile : sourceFiles.keySet()) {
      // Not the cleanest approach
      // TODO: we should write a wrapper class that will handle this for us
      // An option is to use a functional interface for this:
      // https://www.baeldung.com/java-lambda-exceptions
      try {
        // fileIOUtilities.copyFileFromResources(pdiFile);

        byte[] buffer = new byte[sourceFiles.get(pdiFile).available()];
        sourceFiles.get(pdiFile).read(buffer);

        File targetFile = new File(targetFolder + pdiFile);
        outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        // Files.copy(sourceFiles.get(pdiFile), Paths.get("pdiresources/jobs/"));
      } catch (IOException ex) {
        log.error("An IOException occurred while copying resource files", ex);
        return false;
      } // catch (InvalidParameterException paramEx) {
      //  log.error("An Invalid Parameter Exception occurred while copying resource files",
      // paramEx);
      //  return false;
      // }
    }

    return true;
  }
}
