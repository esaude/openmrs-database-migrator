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

  @Test
  public void createDirectory_shouldSucceed() throws IOException {
    Path newDirectory = Paths.get("temp");
    boolean result = fileIOUtilities.createDirectory(newDirectory);

    assertTrue(result);
    assertTrue(Files.exists(newDirectory));

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test
  public void createDirectory_shouldFail_givenDirAlreadyExists() throws IOException {
    Path newDirectory = Paths.get("temp");
    fileIOUtilities.createDirectory(newDirectory);

    boolean result = fileIOUtilities.createDirectory(newDirectory);

    assertFalse(result);

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test
  public void createFile_shouldSucceed() throws IOException {
    Path newDirectory = Paths.get("temp.txt");
    boolean result = fileIOUtilities.createFile(newDirectory);

    assertTrue(result);
    assertTrue(Files.exists(newDirectory));

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test
  public void createFile_shouldFail_givenFileAlreadyExists() throws IOException {
    Path newDirectory = Paths.get("temp.txt");
    fileIOUtilities.createFile(newDirectory);

    boolean result = fileIOUtilities.createFile(newDirectory);

    assertFalse(result);

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test(expected = IOException.class)
  public void createFile_shouldFail_givenFolderDoesNotExists() throws IOException {
    Path newDirectory = Paths.get("temp/temp.txt");

    fileIOUtilities.createFile(newDirectory);
  }

  @Test
  public void copyFileFromResources_shouldSucceed() throws IOException {
    Path file = Paths.get("settings.properties");
    fileIOUtilities.copyFileFromResources(file.toFile().getName());

    assertTrue(Files.exists(file));

    // cleanup
    fileIOUtilities.removeDirectory(file.toFile());
    assertFalse(Files.exists(file));
  }

  @Test(expected = RuntimeException.class)
  public void copyFileFromResources_shouldFail_givenFileUndefined() throws IOException {
    fileIOUtilities.copyFileFromResources(null);
  }

  @Test(expected = IOException.class)
  public void copyFileFromResources_shouldFail_givenFileNotExist() throws IOException {
    fileIOUtilities.copyFileFromResources("unknownFile");
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
          try {
            fileIOUtilities.createDirectory(Paths.get(f));
          } catch (IOException ex) {
            throw new RuntimeException(ex);
          }
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
