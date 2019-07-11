package com.openmrs.migrator.core.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class KettleUtils {

  private static Logger log = LoggerFactory.getLogger(KettleUtils.class);

  private final String KETTLE_PROPERTIES = "kettle.properties";

  private final String KETTLE_DIR = ".kettle";

  public void loadProperties() {

    try (InputStream input = getClass().getResourceAsStream("/pdiresources/" + KETTLE_PROPERTIES)) {

      Properties prop = new Properties();

      log.info(
          "Loading connection details from "
              + KETTLE_PROPERTIES
              + " file in the classpath to system properties");

      prop.load(input);

      log.info(
          "Writing properties to <user home directory>/"
              + KETTLE_DIR
              + "/"
              + KETTLE_PROPERTIES
              + " file");
      writePropertiesToKettlePropertiesOutter(this.getKettlePropertiesLocation(), prop);

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void writePropertiesToKettlePropertiesOutter(File file, Properties properties)
      throws IOException {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {

      bw.write("ETL_SOURCE_DATABASE=" + properties.getProperty("ETL_SOURCE_DATABASE"));
      bw.newLine();
      bw.write("ETL_DATABASE_HOST=" + properties.getProperty("ETL_DATABASE_HOST"));
      bw.newLine();
      bw.write("ETL_DATABASE_PORT=" + properties.getProperty("ETL_DATABASE_PORT"));
      bw.newLine();
      bw.write("ETL_DATABASE_USER=" + properties.getProperty("ETL_DATABASE_USER"));
      bw.newLine();
      bw.write("ETL_DATABASE_PASSWORD=" + properties.getProperty("ETL_DATABASE_PASSWORD"));
    }
  }

  private File getKettlePropertiesLocation() throws IOException {
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
}
