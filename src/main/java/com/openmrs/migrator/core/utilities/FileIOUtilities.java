package com.openmrs.migrator.core.utilities;

import com.openmrs.migrator.core.exceptions.EmptyFileException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileIOUtilities {
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
