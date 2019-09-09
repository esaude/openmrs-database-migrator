package com.openmrs.migrator.core.utilities;

import com.openmrs.migrator.core.exceptions.EmptyFileException;
import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.services.SettingsService;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileIOUtilities {

  private static Logger logger = LoggerFactory.getLogger(FileIOUtilities.class);
  private static final String UPLOADED_FOLDER = "~";
  private Path settingProperties = Paths.get(SettingsService.SETTINGS_PROPERTIES);
  private final String KETTLE_PROPERTIES = "kettle.properties";
  private final String KETTLE_DIR = ".kettle";

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
    InputStream resourceAsStream = getClass().getResourceAsStream(File.separator + resource);
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

  public Optional<String> searchForDataBaseNameInSettingsFile(String databaseName, Path path)
      throws FileNotFoundException, IOException {
    logger.info("Searching database " + databaseName + " in config file");
    try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
      String line;
      while ((line = br.readLine()) != null) {
        String[] keyValue = line.split("=");
        if (SettingsService.SOURCE_DB.equals(keyValue[0]) && (databaseName.equals(keyValue[1]))) {

          logger.info("database name '" + databaseName + "' found in config file");
          return Optional.of(keyValue[1]);
        }
      }
      return Optional.empty();
    }
  }

  public File getKettlePropertiesLocation() throws IOException {
    logger.info("Getting the default location of kettle.properties");
    String homeDirectory = System.getProperty("user.home");

    Path kettleDir = Paths.get(homeDirectory + "/" + KETTLE_DIR);
    Path kettleFile = Paths.get(kettleDir + "/" + KETTLE_PROPERTIES);

    if (!Files.exists(kettleDir)) {
      Files.createDirectories(kettleDir);
      Files.createFile(kettleFile);
    }
    if (!Files.exists(kettleFile)) {
      Files.createFile(kettleFile);
    }

    File file = new File(homeDirectory + "/" + KETTLE_DIR + "/" + KETTLE_PROPERTIES);
    return file;
  }

  public List<String> getAllDataBaseNamesFromConfigFile(Path path)
      throws FileNotFoundException, IOException {
    logger.info("retrieving all data base  names in " + settingProperties + " file");
    List<String> names = new ArrayList<>();
    try (BufferedReader br = new BufferedReader(new FileReader(path.toFile()))) {
      String line;
      while ((line = br.readLine()) != null) {
        if (line.contains(SettingsService.SOURCE_DB)) {
          names.add(line.split("=")[1]);
        }
      }
    }
    return names;
  }

  public void writeToFile(File file, String... contents) throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
      for (String content : contents) {
        bw.append(content);
        bw.newLine();
      }
      bw.flush();
    }
  }

  public List<Path> listFiles(Path path) throws IOException {

    return Files.list(path).collect(Collectors.toList());
  }

  public String getValueFromConfig(String key, String separator, Path path) throws IOException {
    String line;
    String value = null;
    try (BufferedReader bw = new BufferedReader(new FileReader(path.toFile()))) {

      while ((line = bw.readLine()) != null) {
        if (line.contains(key)) {
          value = line.split(separator)[1];
        }
      }
    }
    return value;
  }
  /**
   * @param should be any relative path of folder under the src/main/resources/ for example:
   *     pdiresources/jobs
   * @return List<String>
   * @throws URISyntaxException
   * @throws IOException
   */
  public Map<String, InputStream> getListOfPDIFiles(String folder)
      throws URISyntaxException, IOException {

    Map<String, InputStream> listPDIFiles = new HashMap<>();

    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    Resource[] resources = resolver.getResources(folder);

    for (Resource resource : resources) {
      listPDIFiles.put(resource.getFilename(), resource.getInputStream());
    }

    return listPDIFiles;
  }
}
