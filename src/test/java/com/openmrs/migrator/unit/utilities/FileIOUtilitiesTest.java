package com.openmrs.migrator.unit.utilities;

import static org.junit.Assert.*;

import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileIOUtilitiesTest {

  @Autowired private FileIOUtilities fileIOUtilities;

  private String stream = "pdiresources/jobs/job.kjb";

  @Test
  public void getValidResourceAsStream() throws IOException {

    InputStream inputStream = fileIOUtilities.getResourceAsStream(stream);
    assertNotNull(inputStream);
  }

  @Test(expected = IOException.class)
  public void getInvalidResourceAsStream() throws IOException {

    fileIOUtilities.getResourceAsStream("fake/path");
  } 

  @Test(expected = RuntimeException.class)
  public void removeAllDirectories_shouldFail_givenEmptyList() throws IOException {
    List<String> folders = new ArrayList<>();
    fileIOUtilities.removeAllDirectories(folders);
  }

  @Test(expected = RuntimeException.class)
  public void removeAllDirectories_shouldFail_givenUndefined() throws IOException {
    fileIOUtilities.removeAllDirectories(null);
  }

  @Test()
  public void removeAllDirectories_shouldSucceed() throws IOException {
    List<String> folders = Arrays.asList("folder1", "folder0");

    folders.forEach(
        f -> {
          fileIOUtilities.createDirectory(Paths.get(f));
        });

    fileIOUtilities.removeAllDirectories(folders);

    assertFalse(Files.exists(Paths.get(folders.get(0))));
    assertFalse(Files.exists(Paths.get(folders.get(1))));
  }

  @Test
  public void removeDirectory_shouldSucceed_givenFileExists() throws IOException {
    Path pathToBeDeleted = Paths.get("temp");
    fileIOUtilities.createFile(pathToBeDeleted);

    boolean result = fileIOUtilities.removeDirectory(pathToBeDeleted.toFile());

    assertTrue(result);
    assertFalse("Directory still exists", Files.deleteIfExists(pathToBeDeleted));
  }

  @Test
  public void removeDirectory_shouldFail_givenFileDoesNotExists() throws IOException {
    Path pathToBeDeleted = Paths.get("temp");
    boolean result = fileIOUtilities.removeDirectory(pathToBeDeleted.toFile());

    assertFalse(result);
  }

  @Test(expected = RuntimeException.class)
  public void removeDirectory_shouldFail_givenUndefined() throws IOException {
    fileIOUtilities.removeDirectory(null);
  }
}
