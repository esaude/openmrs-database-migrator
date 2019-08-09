package com.openmrs.migrator.unit.utilities;

import static org.junit.Assert.*;

import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileIOUtilitiesTest {

  @Autowired private FileIOUtilities fileIOUtilities;

  @Autowired private SettingsService settingsService;

  private String stream = SettingsService.PDI_RESOURCES_DIR + "/jobs/job.kjb";

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
  public void createDirectoryShouldSucceed() throws IOException, InvalidParameterException {
    Path newDirectory = Paths.get("temp");
    boolean result = fileIOUtilities.createDirectory(newDirectory);

    assertTrue(result);
    assertTrue(Files.exists(newDirectory));

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test
  public void createDirectoryShouldFailGivenDirAlreadyExists()
      throws IOException, InvalidParameterException {
    Path newDirectory = Paths.get("temp");
    fileIOUtilities.createDirectory(newDirectory);

    boolean result = fileIOUtilities.createDirectory(newDirectory);

    assertFalse(result);

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test
  public void createFileShouldSucceed() throws IOException, InvalidParameterException {
    Path newDirectory = Paths.get("temp.txt");
    boolean result = fileIOUtilities.createFile(newDirectory);

    assertTrue(result);
    assertTrue(Files.exists(newDirectory));

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test
  public void createFileShouldFailGivenFileAlreadyExists()
      throws IOException, InvalidParameterException {
    Path newDirectory = Paths.get("temp.txt");
    fileIOUtilities.createFile(newDirectory);

    boolean result = fileIOUtilities.createFile(newDirectory);

    assertFalse(result);

    // cleanup
    fileIOUtilities.removeDirectory(newDirectory.toFile());
    assertFalse(Files.exists(newDirectory));
  }

  @Test(expected = IOException.class)
  public void createFileShouldFailGivenFolderDoesNotExists() throws IOException {
    Path newDirectory = Paths.get("temp/temp.txt");

    fileIOUtilities.createFile(newDirectory);
  }

  @Test
  public void copyFileFromResourcesShouldSucceed() throws IOException, InvalidParameterException {
    Path file = Paths.get("settings.properties");
    fileIOUtilities.copyFileFromResources(file.toFile().getName());

    assertTrue(Files.exists(file));

    // cleanup
    fileIOUtilities.removeDirectory(file.toFile());
    assertFalse(Files.exists(file));
  }

  @Test(expected = InvalidParameterException.class)
  public void copyFileFromResourcesShouldFailGivenFileUndefined()
      throws IOException, InvalidParameterException {
    fileIOUtilities.copyFileFromResources(null);
  }

  @Test(expected = IOException.class)
  public void copyFileFromResourcesShouldFailGivenFileNotExist()
      throws IOException, InvalidParameterException {
    fileIOUtilities.copyFileFromResources("unknownFile");
  }

  @Test(expected = InvalidParameterException.class)
  public void removeAllDirectoriesShouldFailGivenEmptyList()
      throws IOException, InvalidParameterException {
    List<String> folders = new ArrayList<>();
    fileIOUtilities.removeAllDirectories(folders);
  }

  @Test(expected = InvalidParameterException.class)
  public void removeAllDirectoriesShouldFailGivenUndefined()
      throws IOException, InvalidParameterException {
    fileIOUtilities.removeAllDirectories(null);
  }

  @Test()
  public void removeAllDirectoriesShouldSucceed() throws IOException, InvalidParameterException {
    List<String> folders = Arrays.asList("folder1", "folder0");

    for (String folder : folders) {
      fileIOUtilities.createDirectory(Paths.get(folder));
    }

    fileIOUtilities.removeAllDirectories(folders);

    assertFalse(Files.exists(Paths.get(folders.get(0))));
    assertFalse(Files.exists(Paths.get(folders.get(1))));
  }

  @Test
  public void removeDirectoryShouldSucceedGivenFileExists()
      throws IOException, InvalidParameterException {
    Path pathToBeDeleted = Paths.get("temp");
    fileIOUtilities.createFile(pathToBeDeleted);

    boolean result = fileIOUtilities.removeDirectory(pathToBeDeleted.toFile());

    assertTrue(result);
    assertFalse("Directory still exists", Files.deleteIfExists(pathToBeDeleted));
  }

  @Test
  public void removeDirectoryShouldFailGivenFileDoesNotExists()
      throws IOException, InvalidParameterException {
    Path pathToBeDeleted = Paths.get("temp");
    boolean result = fileIOUtilities.removeDirectory(pathToBeDeleted.toFile());

    assertFalse(result);
  }

  @Test(expected = InvalidParameterException.class)
  public void removeDirectoryShouldFailGivenUndefined()
      throws IOException, InvalidParameterException {
    fileIOUtilities.removeDirectory(null);
  }

  @Test
  public void listFilesShouldListlsExistentFilesInTheDrectory() throws IOException {
    List<Path> paths = fileIOUtilities.listFiles(Paths.get("/etc"));
    // we know all linux ditros have the below file, even FREE_BSD has :)
    assertTrue(paths.contains(Paths.get("/etc/resolv.conf")));
    assertNotNull(paths);
    assertFalse(paths.isEmpty());
  }

  @Test
  public void writeToFileShouldWrite2LinesToTempFile()
      throws IOException, InvalidParameterException {
    Path newDirectory = Paths.get("temp");
    fileIOUtilities.createDirectory(newDirectory);
    fileIOUtilities.createFile(Paths.get("temp/temp_file.txt"));
    fileIOUtilities.writeToFile(Paths.get("temp/temp_file.txt").toFile(), "line1", "line2");
    @SuppressWarnings("resource")
    Stream<String> stream = Files.lines(Paths.get("temp/temp_file.txt"));
    Optional<String> result = stream.reduce((a, b) -> a + b);

    assertEquals("line1line2", result.get());
    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test(expected = FileNotFoundException.class)
  public void writeToFileShouldThrowAnException() throws IOException, InvalidParameterException {
    Path path = Paths.get("/not_unix_like_directory/");
    fileIOUtilities.writeToFile(path.toFile(), "line1", "line2");
  }

  @Test
  public void getValueFromConfigShouldReturnValueFromFile()
      throws IOException, InvalidParameterException {
    Path newDirectory = Paths.get("temp");

    fileIOUtilities.createDirectory(newDirectory);
    fileIOUtilities.createFile(Paths.get("temp/temp_file.txt"));
    Path path = Paths.get("temp/temp_file.txt");

    fileIOUtilities.writeToFile(path.toFile(), SettingsService.DB_HOST + "=0.0.0.0");

    String value = fileIOUtilities.getValueFromConfig(SettingsService.DB_HOST, "=", path);

    assertEquals("0.0.0.0", value);
    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test
  public void getValueFromConfigShouldReturnNull() throws IOException, InvalidParameterException {
    Path newDirectory = Paths.get("temp");

    fileIOUtilities.createDirectory(newDirectory);
    fileIOUtilities.createFile(Paths.get("temp/temp_file.txt"));
    Path path = Paths.get("temp/temp_file.txt");

    fileIOUtilities.writeToFile(path.toFile(), "not_to_be_found_label" + "=any_value");

    String value = fileIOUtilities.getValueFromConfig("label_not_to_be_found", "=", path);

    assertNull(value);
    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test
  public void getAllDataBaseNamesFromConfigFileSShouldReturnDBName()
      throws IOException, InvalidParameterException {

    Path newDirectory = Paths.get("temp");

    fileIOUtilities.createDirectory(newDirectory);
    fileIOUtilities.createFile(Paths.get("temp/temp_file.txt"));
    Path path = Paths.get("temp/temp_file.txt");

    fileIOUtilities.writeToFile(path.toFile(), SettingsService.DB + "=data_base");

    List<String> names = fileIOUtilities.getAllDataBaseNamesFromConfigFile(path);
    assertEquals(1, names.size());
    assertEquals("data_base", names.get(0));
    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test
  public void getAllDataBaseNamesFromConfigFileShouldReturnEmptyList()
      throws IOException, InvalidParameterException {

    Path newDirectory = Paths.get("temp");

    fileIOUtilities.createDirectory(newDirectory);
    fileIOUtilities.createFile(Paths.get("temp/temp_file.txt"));
    Path path = Paths.get("temp/temp_file.txt");

    List<String> names = fileIOUtilities.getAllDataBaseNamesFromConfigFile(path);
    assertTrue(names.isEmpty());
    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test
  public void searchForDataBaseNameInSettingsFileShouldReturnOneDBName()
      throws IOException, InvalidParameterException {

    Path newDirectory = Paths.get("temp");

    fileIOUtilities.createDirectory(newDirectory);
    fileIOUtilities.createFile(Paths.get("temp/temp_file.txt"));
    Path path = Paths.get("temp/temp_file.txt");

    fileIOUtilities.writeToFile(path.toFile(), SettingsService.DB + "=fgh");
    Optional<String> name = fileIOUtilities.searchForDataBaseNameInSettingsFile("fgh", path);

    assertEquals("fgh", name.get());

    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test
  public void searchForDataBaseNameInSettingsFileShouldReturnEmptyOptonalValue()
      throws IOException, InvalidParameterException {

    Path newDirectory = Paths.get("temp");

    fileIOUtilities.createDirectory(newDirectory);
    fileIOUtilities.createFile(Paths.get("temp/temp_file.txt"));
    Path path = Paths.get("temp/temp_file.txt");

    fileIOUtilities.writeToFile(path.toFile(), "unknown_label_attribute" + "=fgh");
    Optional<String> name = fileIOUtilities.searchForDataBaseNameInSettingsFile("fgh", path);

    assertFalse(name.isPresent());

    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test
  public void setConnectionToKettleFileShouldWriteToKettleFile()
      throws IOException, InvalidParameterException {

    Path newDirectory = Paths.get("temp");
    fileIOUtilities.createDirectory(newDirectory);

    Path settingPropeties = Paths.get("temp/setting.propeties");
    Path kettleFile = Paths.get("temp/kettle.propeties");

    fileIOUtilities.createFile(settingPropeties);
    fileIOUtilities.createFile(kettleFile);

    Map<String, String> connectionMap = new HashMap<>();
    connectionMap.put(SettingsService.DB_USER, "user");
    connectionMap.put(SettingsService.DB_PASS, "pass");
    connectionMap.put(SettingsService.DB_HOST, "localhost");
    connectionMap.put(SettingsService.DB_PORT, "1234");

    settingsService.fillConfigFile(settingPropeties, connectionMap);

    assertEquals(4, Files.lines(settingPropeties).count());
    assertEquals(
        "user", fileIOUtilities.getValueFromConfig(SettingsService.DB_USER, "=", settingPropeties));
    assertEquals(
        "pass", fileIOUtilities.getValueFromConfig(SettingsService.DB_PASS, "=", settingPropeties));
    assertEquals(
        "localhost",
        fileIOUtilities.getValueFromConfig(SettingsService.DB_HOST, "=", settingPropeties));
    assertEquals(
        "1234", fileIOUtilities.getValueFromConfig(SettingsService.DB_PORT, "=", settingPropeties));

    fileIOUtilities.setConnectionToKettleFile("icap", settingPropeties, kettleFile.toFile());
    assertEquals(
        "user", fileIOUtilities.getValueFromConfig(SettingsService.DB_USER, "=", kettleFile));
    assertEquals(
        "pass", fileIOUtilities.getValueFromConfig(SettingsService.DB_PASS, "=", kettleFile));
    assertEquals(
        "localhost", fileIOUtilities.getValueFromConfig(SettingsService.DB_HOST, "=", kettleFile));
    assertEquals(
        "1234", fileIOUtilities.getValueFromConfig(SettingsService.DB_PORT, "=", kettleFile));

    fileIOUtilities.removeDirectory(newDirectory.toFile());
  }

  @Test
  public void getKettlePropertiesLocationShouldReturnKettleFile() throws IOException {
    File kettleFile = fileIOUtilities.getKettlePropertiesLocation();

    assertNotNull(kettleFile);
    assertTrue(kettleFile.exists());
  }
}
