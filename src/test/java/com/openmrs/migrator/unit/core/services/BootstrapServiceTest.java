package com.openmrs.migrator.unit.core.services;

import static org.junit.Assert.*;

import com.openmrs.migrator.core.services.BootstrapService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  private String settingsFile = "settings.properties";

  @Before
  public void setUp() throws IOException {
    fileIOUtils.removeAllDirectories(folders);
    fileIOUtils.removeDirectory(Paths.get(settingsFile).toFile());
  }

  @Test
  public void createDirectoryStructureSuccess() throws IOException {
    bootstrapService.createDirectoryStructure(folders);

    assertTrue(Files.exists(Paths.get(folders.get(0))));
    assertTrue(Files.exists(Paths.get(folders.get(1))));
    assertTrue(Files.exists(Paths.get(folders.get(2))));
  }

  @Test
  public void populateDefaultResoucesSuccess() throws IOException {
    bootstrapService.createDirectoryStructure(folders);

    List<String> pdiFiles = new ArrayList<>();
    pdiFiles.add("pdiresources/jobs/job.kjb");
    pdiFiles.add("pdiresources/transformations/transformation.ktr");
    pdiFiles.add(settingsFile);

    bootstrapService.populateDefaultResources(pdiFiles);

    assertTrue(Files.exists(Paths.get(pdiFiles.get(0))));
    assertTrue(Files.exists(Paths.get(pdiFiles.get(1))));
    assertTrue(Files.exists(Paths.get(pdiFiles.get(2))));
  }

  @Test(expected = NullPointerException.class)
  public void populateDefaultResoucesFail_givenFoldersUndefined() throws IOException {
    bootstrapService.populateDefaultResources(null);
  }

  @Test(expected = RuntimeException.class)
  public void populateDefaultResoucesFail_givenFolderStructureDoesntExist() throws IOException {
    List<String> pdiFiles = new ArrayList<>();
    pdiFiles.add("pdiresources/jobs/job.kjb");

    bootstrapService.populateDefaultResources(pdiFiles);
  }

  @After
  public void cleanUp() throws IOException {
    fileIOUtils.removeAllDirectories(folders);
    fileIOUtils.removeDirectory(Paths.get(settingsFile).toFile());
  }
}
