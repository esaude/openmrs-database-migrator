package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PDIServiceImpl implements PDIService {

  private static Logger LOG = LoggerFactory.getLogger(PDIServiceImpl.class);

  private final String[] transformations = {"pdiresources/transformations/merge-patient.ktr"};

  private final String KETTLE_PROPERTIES = "kettle.properties";

  private final String KETTLE_DIR = ".kettle";

  @Autowired private FileIOUtilities fileIOUtilities;

  @Override
  public void runTransformation(InputStream xmlStream) throws KettleException {

    this.loadProperties();

    KettleEnvironment.init();

    TransMeta transMeta = new TransMeta(xmlStream, null, true, null, null);

    Trans transformation = new Trans(transMeta);
    transformation.setLogLevel(LogLevel.BASIC);

    String name = transformation.getName();
    LOG.info("Starting transformation " + name);

    transformation.execute(new String[0]);
    transformation.waitUntilFinished();

    Result result = transformation.getResult();

    String outcome =
        String.format(
            "Transformation %s executed %s",
            name,
            (result.getNrErrors() == 0
                ? "successfully"
                : "with " + result.getNrErrors() + " errors"));

    LOG.info(outcome);
  }

  @Override
  public void mergeOpenMRS() {
    try {

      for (String t : transformations) {
        InputStream xml = fileIOUtilities.getResourceAsStream(t);
        runTransformation(xml);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (KettleException e) {
      // Do nothing kettle prints stack trace
    }
  }

  private void loadProperties() {

    try (InputStream input = getClass().getResourceAsStream("/pdiresources/" + KETTLE_PROPERTIES)) {

      Properties prop = new Properties();

      LOG.info(
          "Loading connection details from "
              + KETTLE_PROPERTIES
              + " file in the classpath to system properties");

      prop.load(input);

      LOG.info(
          "Writing properties to <user home directory>/"
              + KETTLE_DIR
              + "/"
              + KETTLE_PROPERTIES
              + " file");

      File kettleFile = getKettlePropertiesLocation();

      writePropertiesToKettlePropertiesOutter(kettleFile, prop);

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
