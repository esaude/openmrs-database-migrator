package com.openmrs.migrator.core.utilities;

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
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Component;

@Component
public class FileIOUtilities {

  private static Logger logger = LoggerFactory.getLogger(FileIOUtilities.class);
  private Path settingProperties = Paths.get(SettingsService.SETTINGS_PROPERTIES);
  private List<String> dirList = new ArrayList<>();

  private Map<String, InputStream> map = new HashMap<>();

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
   * Collect all resource files from specific <b>resourceFolder</b>. The <b>resourceFolder</b>
   * should follow the pattern: <i>classpath*:path/to/folder/*
   *
   * @param resourceFolder
   * @return Map<String, InputStream>
   * @throws URISyntaxException
   * @throws IOException
   */
  public Map<String, InputStream> getListOfResourceFiles(String resourceFolder)
      throws URISyntaxException, IOException {

    String superParent = resourceFolder.substring(0, resourceFolder.length() - 1).split(":")[1];

    ClassLoader cl = this.getClass().getClassLoader();
    ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
    Resource[] resources = resolver.getResources(resourceFolder);

    for (Resource r : resources) {
      if (!r.isReadable()) {
        getListOfResourceFiles("classpath:" + superParent + r.getFilename() + "/*");
      } else {
        map.put(superParent + r.getFilename(), r.getInputStream());
      }
    }

    return map;
  }

  public boolean isSettingsFilesMissingSomeValue() throws IOException {

    BufferedReader br = new BufferedReader(new FileReader(new File("settings.properties")));
    String line = null;
    while ((line = br.readLine()) != null) {
      String[] prop = line.split("=");
      if (prop.length == 1
          || (("ETL_DATABASE_HOST".equals(prop[0])
                  || "ETL_DATABASE_PORT".equals(prop[0])
                  || "ETL_DATABASE_USER".equals(prop[0])
                  || "ETL_DATABASE_PASSWORD".equals(prop[0])
                  || "ETL_SOURCE_DATABASE".equals(prop[0]))
              && (StringUtils.isBlank(prop[1].trim())))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Lists all files's paths under the <b>parent</b> folder,for better coverage its recommended that
   * the given <b>parent</b> folder its located at root of the <i>classpath</i>. This works for
   * <i>jar</i> environment and <i>IDE</i> environment.
   *
   * @param parent
   * @return List<String>
   * @throws IOException
   * @throws URISyntaxException
   */
  public List<String> identifyResourceSubFolders(String parent)
      throws IOException, URISyntaxException {

    Enumeration<URL> urls = this.getClass().getClassLoader().getResources(parent);

    URL url = urls.nextElement();
    URL urljar = this.getClass().getClassLoader().getResource(parent);

    if (urljar.getProtocol().equals("jar")) {
      String jarPath = urljar.getPath().substring(5, urljar.getPath().indexOf("!"));
      try (JarFile jar = new JarFile(URLDecoder.decode(jarPath, "UTF-8"))) {
        Enumeration<JarEntry> entries = jar.entries();
        Set<String> set = new HashSet<>();
        while (entries.hasMoreElements()) {
          String s = entries.nextElement().toString();
          if (s.contains(parent)) {
            set.add(s);
          }
        }
        List<String> list = set.stream().map(x -> x.substring(17)).collect(Collectors.toList());
        return list;
      }

    } else {
      File dir = new File(url.toURI());
      for (File nextFile : dir.listFiles()) {
        if (nextFile.isDirectory()) {
          identifyResourceSubFolders(parent + nextFile.getName() + "/");
        }
        dirList.add(parent + nextFile.getName());
      }
    }

    return dirList;
  }

  /**
   * prepares the each element of the <i>listOfPaths</i> to pattern:
   * <i>classpath*:path/to/folder/*</i> and the return a Set to avoid path duplications
   *
   * @param listOfPaths
   * @return Set<String>
   */
  public Set<String> prepareResourceFolder(List<String> listOfPaths, String partOfFileExtention) {
    Set<String> set =
        listOfPaths.stream()
            .filter(x -> x.contains(partOfFileExtention))
            .map(
                x -> {
                  String[] vect = x.split("/");
                  StringBuilder sb = new StringBuilder();
                  for (String string : vect) {
                    if (!string.contains(partOfFileExtention)) {
                      sb.append(string + "/");
                    }
                  }
                  return sb.toString();
                })
            .map(x -> "classpath:" + x + "*")
            .collect(Collectors.toSet());
    return set;
  }
}
