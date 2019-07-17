package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.BootstrapService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BootstrapServiceImpl implements BootstrapService {

  private static Logger log = LoggerFactory.getLogger(BootstrapServiceImpl.class);

  int numberOfCreatedFiles = 0;

  private final Path TARGET_JOB_FOLDER = Paths.get("pdiresources/jobs/");

  private final Path TARGET_TRANSFORMATION_FOLDER = Paths.get("pdiresources/transformations/");

  private final String JOB_FOLDER = "pdiresources/jobs/";

  private final String TRANSFORMATION_FOLDER = "pdiresources/transformations/";

  /**
   * Creates folders structure, if a one of the folder exists a warn log is raised informing that
   * the folder or won't be created because it already exists
   */
  @Override
  public int createDirectoryStructure(List<String> dirList, Path settingsProperties)
      throws IOException {

    log.info("Starting creating folder structure");

    dirList.forEach(
        x -> {
          if (!checkIfPathExist(Paths.get(x))) {
            try {
              Files.createDirectory(Paths.get(x));
              log.info("Folder: " + x + " created sucessfully");
              if (x.equals("pdiresources/")) {
                Files.createDirectory(TARGET_JOB_FOLDER);
                Files.createDirectory(TARGET_TRANSFORMATION_FOLDER);
              }
              numberOfCreatedFiles++;
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            log.warn("Folder: " + x + " will not be created, folder already exists");
          }
        });

    if (!checkIfPathExist(settingsProperties)) {
      Files.createFile(settingsProperties);
      numberOfCreatedFiles++;
      log.info("File: settings.properties created sucessfully");
    } else {
      log.warn("File: settings.properties will not be created file already exists");
    }

    return numberOfCreatedFiles;
  }

  private boolean checkIfPathExist(Path path) {

    return Files.exists(path);
  }

  /** this method copies resources of the app to folder where the app is running */
  @Override
  public void populateDefaultResouce() throws IOException {

    log.info("Starting populating PDi folders  ");

    Set<String> pdiFiles = new HashSet<>();

    pdiFiles.add(JOB_FOLDER + "merge-patient-job.kjb");
    pdiFiles.add(TRANSFORMATION_FOLDER + "merge-patient.ktr");

    pdiFiles.forEach(
        pdiFile -> {
          Path pdiPath = Paths.get(pdiFile);
          if (Files.notExists(Paths.get(pdiFile))) {

            try {
              Files.createFile(pdiPath);
            } catch (IOException e) {

              e.printStackTrace();
            }
            log.info("File:" + pdiPath.getFileName() + " copied to " + pdiPath.getParent());
          } else {
            log.warn("File:" + pdiPath.getFileName() + " already exist in " + pdiPath.getParent());
          }
        });

  }
}
