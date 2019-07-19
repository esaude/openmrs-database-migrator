package com.openmrs.migrator.core.utilities;

import com.openmrs.migrator.core.exceptions.EmptyFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
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
      throw new EmptyFileException();
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
   */
  public boolean createDirectory(Path directoryToCreate) {
    if (!checkIfPathExist(directoryToCreate)) {
      try {
        Files.createDirectory(directoryToCreate);
        logger.info("Folder: " + directoryToCreate + " created sucessfully");

        return true;
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
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
  public boolean createFile(Path fileName) {
    if (!checkIfPathExist(fileName)) {
      try {
        Files.createFile(fileName);
        logger.info("File: " + fileName + " created successfully");
      } catch (IOException e) {
        e.printStackTrace();
        return false;
      }
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
   */
  public void copyFileFromResources(String resourceFile) throws IOException {
    if (resourceFile == null || resourceFile.isEmpty()) {
      throw new RuntimeException("No file specified");
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
  public void removeAllDirectories(List<String> directories) throws IOException {
    if (directories == null || directories.size() == 0) {
      throw new IOException("List of directories is empty or undefined");
    }

    directories.forEach(
        directory -> {
          removeDirectory(new File(directory));
        });
  }

  /**
   * Recursively delete a directory and all its contents
   *
   * @param directoryToBeDeleted
   * @return boolean value indicating success or failure
   */
  public boolean removeDirectory(File directoryToBeDeleted) {
    if (directoryToBeDeleted == null) {
      return false;
    }

    File[] allContents = directoryToBeDeleted.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        removeDirectory(file);
      }
    }
    return directoryToBeDeleted.delete();
  }
}
