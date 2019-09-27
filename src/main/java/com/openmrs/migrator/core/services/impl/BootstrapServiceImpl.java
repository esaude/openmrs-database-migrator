package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.exceptions.InvalidParameterException;
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
   * this method receives the stream of resource files and writes to relative path
   *
   * @param Map<String, InputStream> sourceFiles
   * @return boolean indicating success or failure
   * @throws InvalidParameterException
   */
  @Override
  public boolean populateDefaultResources(Map<String, InputStream> sourceFiles)
      throws IOException, InvalidParameterException {
    log.info("Populating PDI folders with default resources");
    FileOutputStream outStream;

    for (String pdiFile : sourceFiles.keySet()) {

      try {
        decomposePath(pdiFile);
        byte[] buffer = new byte[sourceFiles.get(pdiFile).available()];
        if (pdiFile.contains(".jar")) {
          fileIOUtilities.copyFileFromResources(pdiFile);
          continue;
        }
        sourceFiles.get(pdiFile).read(buffer);

        File targetFile = new File(pdiFile);
        outStream = new FileOutputStream(targetFile);
        outStream.write(buffer);
        outStream.flush();
        log.info("File: " + pdiFile + " created");
      } catch (IOException ex) {
        log.error("An IOException occurred while copying resource files", ex);
        return false;
      }
    }

    return true;
  }
  /**
   * This helper method supports on folder creation
   *
   * @param string
   * @throws IOException
   */
  private void decomposePath(String string) throws IOException {
    if (string.contains("/")) {
      String[] splitted = string.split("/");
      StringBuilder acumulator = new StringBuilder("");
      for (int i = 0; i < splitted.length; i++) {
        // if it is not the last element
        if (i < splitted.length - 1) {
          acumulator.append(splitted[i] + "/");
          fileIOUtilities.createDirectory(Paths.get(acumulator.toString()));
          log.info("Folder: " + acumulator.toString() + " created");
        }
      }
    }
  }
}
