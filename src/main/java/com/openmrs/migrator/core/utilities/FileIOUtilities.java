package com.openmrs.migrator.core.utilities;

import com.openmrs.migrator.core.exceptions.EmptyFileException;
import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.model.DataBaseConnectionDetail;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileIOUtilities {

  private static Logger logger = LoggerFactory.getLogger(FileIOUtilities.class);
  private static final String UPLOADED_FOLDER = "~";

  public void UploadFile(MultipartFile file) throws EmptyFileException {
    if (file.isEmpty()) {
      throw new EmptyFileException(file.getOriginalFilename());
    }

    try {
      // Get the file and save it somewhere
      byte[] bytes = file.getBytes();
      Path path = Paths.get(UPLOADED_FOLDER + file.getOriginalFilename());
      Files.write(path, bytes);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads resources from the jar file
   *
   * @param resource The resource file name
   * @return An input stream for the resource file
   * @throws IOException if the resource could not be found
   */
  public InputStream getResourceAsStream(String resource) throws IOException {
    InputStream resourceAsStream = getClass().getResourceAsStream("/" + resource);
    if (resourceAsStream == null) {
      throw new IOException("Could not load resource " + resource);
    }
    return resourceAsStream;
  }

  /**
   * Check if a path exists
   *
   * @param path
   * @return
   */
  public boolean checkIfPathExist(Path path) {
    return Files.exists(path);
  }

  /**
   * Create a directory
   *
   * @param directoryToCreate
   * @return boolean indicating if directory has been created successfully or not
   * @throws IOException
   */
  public boolean createDirectory(Path directoryToCreate) throws IOException {
    if (!checkIfPathExist(directoryToCreate)) {
      Files.createDirectory(directoryToCreate);
      logger.info("Folder: " + directoryToCreate + " created sucessfully");

      return true;
    } else {
      logger.warn("Folder: " + directoryToCreate + " will not be created, folder already exists");
      return false;
    }
  }

  /**
   * Create a new file by passing in a Path Object
   *
   * @param fileName
   * @return boolean indicating if file has been created successfully or not
   * @throws IOException
   */
  public boolean createFile(Path fileName) throws IOException {
    if (!checkIfPathExist(fileName)) {
      Files.createFile(fileName);
      logger.info("File: " + fileName + " created successfully");
      return true;
    } else {
      logger.warn("File: " + fileName + " will not be created since file already exists");
      return false;
    }
  }

  /**
   * Copy a file from local resources folder to same directory that the app is running in
   *
   * @param resourceFile
   * @throws IOException
   * @throws InvalidParameterException
   */
  public void copyFileFromResources(String resourceFile)
      throws IOException, InvalidParameterException {
    if (resourceFile == null || resourceFile.isEmpty()) {
      throw new InvalidParameterException(resourceFile);
    }

    // read the files form the resources folder in the jar application
    InputStream resourceStream = getResourceAsStream(resourceFile);

    // copy files from resources to home directory
    Files.copy(resourceStream, Paths.get(resourceFile), StandardCopyOption.REPLACE_EXISTING);
  }

  /**
   * Takes in list of directory paths as strings and removes each directory even when they are
   * empty.
   *
   * @param directories
   * @throws IOException
   */
  public void removeAllDirectories(List<String> directories)
      throws IOException, InvalidParameterException {
    if (directories == null || directories.size() == 0) {
      throw new InvalidParameterException(directories);
    }

    for (String dir : directories) {
      removeDirectory(new File(dir));
    }
  }

  /**
   * Recursively delete a directory or file and all its contents
   *
   * @param directoryToBeDeleted
   * @return boolean value indicating success or failure
   * @throws InvalidParameterException
   */
  public boolean removeDirectory(File directoryToBeDeleted) throws InvalidParameterException {
    if (directoryToBeDeleted == null) {
      throw new InvalidParameterException(directoryToBeDeleted);
    }

    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        removeDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }

  /**
   * Reads setting.properties and loads the data to DataBaseConnectionDetail
   *
   * @return DataBaseConnectionDetail
   * @throws IOException
   */
  public Optional<DataBaseConnectionDetail> readSettingFiles() throws IOException {

    DataBaseConnectionDetail baseDetail = new DataBaseConnectionDetail();
    Path settingProperties = Paths.get("settings.properties");
    try (BufferedReader br = new BufferedReader(new FileReader(settingProperties.toFile()))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] entry = line.split("=");
        if ("name".equals(entry[0])) {
          baseDetail.setDataBaseName(entry[1]);
        }
        if ("username".equals(entry[0])) {
          baseDetail.setUsername(entry[1]);
        }
        if ("password".equals(entry[0])) {
          baseDetail.setPassword(entry[1]);
        }
      }
    }

    return Optional.of(baseDetail);
  }

  public boolean isSettingFound() {

    Path settingProperties = Paths.get("settings.properties");

    if (settingProperties.toFile().length() == 0) {
      return true;
    }
    return false;
  }

  public void addSettingToConfigFile(DataBaseConnectionDetail dataBaseConnectionDetail)
      throws IOException {
    Path settingProperties = Paths.get("settings.properties");
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(settingProperties.toFile()))) {

      bw.write(dataBaseConnectionDetail.getDataBaseName());
      bw.newLine();
      bw.write(dataBaseConnectionDetail.getUsername());
      bw.newLine();
      bw.write(dataBaseConnectionDetail.getPassword());
      bw.newLine();

      bw.flush();
    }
  }
}
