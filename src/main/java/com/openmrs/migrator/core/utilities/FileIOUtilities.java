package com.openmrs.migrator.core.utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.web.multipart.MultipartFile;

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

  private class EmptyFileException extends Throwable {}
}
