package com.openmrs.migrator.unit.core.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.openmrs.migrator.core.exceptions.InvalidParameterException;
import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BootstrapServiceTest {

  @Autowired private BootstrapService bootstrapService;

  @Autowired private FileIOUtilities fileIOUtils;

  private List<String> folders =
      Arrays.asList(
          "input", "output", "pdiresources", "pdiresources/transformations", "pdiresources/jobs");

  @Before
  public void setUp() throws IOException, InvalidParameterException {
    fileIOUtils.removeAllDirectories(folders);
    fileIOUtils.removeDirectory(Paths.get(SettingsService.SETTINGS_PROPERTIES).toFile());
  }

  @Test
  public void createDirectoryStructureSuccess() throws IOException {
    boolean result = bootstrapService.createDirectoryStructure(folders);

    assertTrue(result);
    assertTrue(Files.exists(Paths.get(folders.get(0))));
    assertTrue(Files.exists(Paths.get(folders.get(1))));
    assertTrue(Files.exists(Paths.get(folders.get(2))));
  }

  @Test
  public void createDirectoryStructureFailureShouldThrowIOExceptionWithMissingPaths()
      throws IOException {
    boolean result = bootstrapService.createDirectoryStructure(Arrays.asList("/missing/"));

    assertFalse(result);
    assertFalse(Files.exists(Paths.get(folders.get(0))));
  }

  @Test
  public void populateDefaultResoucesSuccess()
      throws IOException, URISyntaxException, InvalidParameterException {
    String pdiFolder = "classpath:" + SettingsService.PDI_RESOURCES_DIR + "/*";

    Map<String, InputStream> files = fileIOUtils.getListOfResourceFiles(pdiFolder);

    bootstrapService.populateDefaultResources(files);

    files.keySet().forEach(System.out::println);

    assertFalse(files.isEmpty());
  }

  @Test(expected = NullPointerException.class)
  public void populateDefaultResoucesFailGivenFoldersUndefined()
      throws IOException, InvalidParameterException {
    bootstrapService.populateDefaultResources(null);
  }

  @Test
  public void populateDefaultResoucesFailGivenFolderStructureDoesntExist()
      throws IOException, InvalidParameterException {
    List<String> pdiFiles = new ArrayList<>();
    pdiFiles.add("pdiresources/jobs/job.kjb");
    File file = new File("").createTempFile("pdiresources/jobs/job", "kjb");
    Map<String, InputStream> sourceFiles = new HashMap<>();
    sourceFiles.put("pdiresources/jobs/job.kjb", new FileInputStream(file));
    boolean result = bootstrapService.populateDefaultResources(sourceFiles);

    assertTrue(result);
  }

  @Test
  public void populateDefaultResourcesShouldCatchIOIfFileIsMissing()
      throws IOException, InvalidParameterException {
    List<String> pdiFiles = new ArrayList<>();
    pdiFiles.add("pdiresources/jobs/job.kjb");
    File file = new File("").createTempFile("pdiresources/jobs/job", "kjb");
    Map<String, InputStream> sourceFiles = new HashMap<>();
    sourceFiles.put("pdiresources/jobs/job.csv", new FileInputStream(file));
    boolean result = bootstrapService.populateDefaultResources(sourceFiles);

    assertFalse(result);
  }

  @After
  public void cleanUp() throws IOException, InvalidParameterException {
    fileIOUtils.removeAllDirectories(folders);
    fileIOUtils.removeDirectory(Paths.get(SettingsService.SETTINGS_PROPERTIES).toFile());
  }
}
