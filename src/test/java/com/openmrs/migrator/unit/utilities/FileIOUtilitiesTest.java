package com.openmrs.migrator.unit.utilities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
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
    Path file = Paths.get(SettingsService.SETTINGS_PROPERTIES);
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

    fileIOUtilities.writeToFile(path.toFile(), SettingsService.SOURCE_DB + "=data_base");

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

    fileIOUtilities.writeToFile(path.toFile(), SettingsService.SOURCE_DB + "=fgh");
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
  public void getListOfPDIFilesInResourcesShouldReturnListofFiles()
      throws URISyntaxException, IOException {

    String pdiFolder = "classpath:" + SettingsService.PDI_RESOURCES_DIR + "/*";
    Map<String, InputStream> files = fileIOUtilities.getListOfResourceFiles(pdiFolder);

    files.keySet().forEach(x -> System.out.println(files.get(x) + " ::::: " + x));
    assertFalse(files.isEmpty());
  }

  @Test
  public void isSettingsFilesMissingSomeValueShouldReturnTrue() throws IOException {

    File file = new File("settings.properties");
    file.createNewFile();

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
      bw.write("ETL_SOURCE_DATABASE=");
      bw.flush();
    }

    boolean value = fileIOUtilities.isSettingsFilesMissingSomeValue();

    assertTrue(value);
  }

  @Test
  public void isSettingsFilesMissingSomeValueShouldReturnfalse() throws IOException {

    File file = new File("settings.properties");
    file.createNewFile();

    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
      bw.write("ETL_SOURCE_DATABASE=fgh\n");
      bw.flush();
      bw.write("ETL_DATABASE_HOST=127.0.0.1\n");
      bw.flush();
      bw.write("ETL_DATABASE_PORT=3306\n");
      bw.flush();
      bw.write("ETL_DATABASE_USER=root\n");
      bw.flush();
      bw.write("ETL_DATABASE_PASSWORD=password\n");
      bw.flush();
    }

    boolean value = fileIOUtilities.isSettingsFilesMissingSomeValue();

    assertFalse(value);
  }

  @Test
  public void identifyResourceSubFoldersShouldRunSuccess() throws IOException, URISyntaxException {
    List<String> list =
        fileIOUtilities.identifyResourceSubFolders(SettingsService.PDI_RESOURCES_DIR + "/");
    list.forEach(System.out::println);
    assertNotNull(list);
    assertFalse(list.isEmpty());
  }

  @Test
  public void prepareResourceFolderShouldRunSuccess() {

    List<String> dirList =
        Arrays.asList(
            SettingsService.PDI_RESOURCES_DIR + "/jobs/dummy1.kjb",
            SettingsService.PDI_RESOURCES_DIR + "/jobs/migration-jobs/dummy2.kjb*");

    Set<String> set = fileIOUtilities.prepareResourceFolder(dirList, ".k");
    set.forEach(System.out::println);

    assertFalse(set.isEmpty());
    assertTrue(set.contains("classpath:pdiresources/jobs/migration-jobs/*"));
    assertTrue(set.contains("classpath:pdiresources/jobs/*"));
  }
}
