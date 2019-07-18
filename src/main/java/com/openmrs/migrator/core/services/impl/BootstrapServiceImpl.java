package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.BootstrapService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BootstrapServiceImpl implements BootstrapService {

  private static Logger log = LoggerFactory.getLogger(BootstrapServiceImpl.class);

  private final Path TARGET_JOB_FOLDER = Paths.get("pdiresources/jobs/");

  private final Path TARGET_TRANSFORMATION_FOLDER = Paths.get("pdiresources/transformations/");

  /**
   * Creates folders structure. If one of the folders exists, a warn log is raised informing the
   * folder won't be created because it already exists
   *
   * @return Stream of created files
   */
  @Override
  public Stream<String> createDirectoryStructure(List<String> dirList, Path settingsProperties)
      throws IOException {
    List<String> directoryStream = new ArrayList<>();

    log.info("Starting creating folder structure");

    dirList.forEach(
        dir -> {
          if (!checkIfPathExist(Paths.get(dir))) {
            try {
              Files.createDirectory(Paths.get(dir));
              log.info("Folder: " + dir + " created sucessfully");
              if ("pdiresources/".equals(dir)) {
                Files.createDirectory(TARGET_JOB_FOLDER);
                Files.createDirectory(TARGET_TRANSFORMATION_FOLDER);
              }
              directoryStream.add(dir);
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            log.warn("Folder: " + dir + " will not be created, folder already exists");
          }
        });

    if (!checkIfPathExist(settingsProperties)) {
      Files.createFile(settingsProperties);
      log.info("File: settings.properties created sucessfully");
      directoryStream.add(settingsProperties.getFileName().toString());
    } else {
      log.warn("File: settings.properties will not be created file already exists");
    }

    return directoryStream.stream();
  }

  private boolean checkIfPathExist(Path path) {

    return Files.exists(path);
  }

  /**
   * this method copies resources of the app to folder where the app is running
   *
   * @return Set of created files
   */
  @Override
  public Set<String> populateDefaultResource(Set<String> sourceFiles) throws IOException {

    Set<String> createdFiles = new HashSet<>();

    log.info("Starting populating PDi folders  ");

    sourceFiles.forEach(
        pdiFile -> {
          Path pdiPath = Paths.get(pdiFile);
          if (Files.notExists(Paths.get(pdiFile))) {

            try {
              Files.createFile(pdiPath);
              log.info("File:" + pdiPath.getFileName() + " copied to " + pdiPath.getParent());
              createdFiles.add(pdiFile);
            } catch (IOException e) {
              e.printStackTrace();
            }
          } else {
            log.warn("File:" + pdiPath.getFileName() + " already exist in " + pdiPath.getParent());
          }
        });

    return createdFiles;
  }
}
