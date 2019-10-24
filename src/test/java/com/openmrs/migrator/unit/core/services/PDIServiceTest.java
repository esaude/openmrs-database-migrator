package com.openmrs.migrator.unit.core.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PDIServiceTest {

  @Autowired private PDIService pdiService;

  @Autowired private FileIOUtilities fileIOUtilities;

  private String[] jobs = {
    SettingsService.PDI_RESOURCES_DIR + "/jobs/job.kjb",
    SettingsService.PDI_RESOURCES_DIR + "/jobs/job-invalid.kjb"
  };

  private InputStream streamWithValidJob, streamWithInValidJob;

  @Before
  public void setUp() throws Exception {
    streamWithValidJob = fileIOUtilities.getResourceAsStream(jobs[0]);
    streamWithInValidJob = fileIOUtilities.getResourceAsStream(jobs[1]);
  }

  @Test
  public void runJobSuccess() throws SettingsException {
    boolean runnedCorrectly = pdiService.runJob(streamWithValidJob);
    assertTrue(runnedCorrectly);
  }

  @Test(expected = SettingsException.class)
  public void runJobShouldThrowSettingeExptionWithWrongOrNullForJobXML() throws SettingsException {
    pdiService.runJob(null);
  }

  @Ignore
  @Test
  public void runMainJob() throws SettingsException, IOException {
    boolean runnedCorrectly =
        pdiService.runJob(
            new FileInputStream("src/main/resources/pdiresources/jobs/control-center.kjb"));
    assertTrue(runnedCorrectly);
  }

  @Test
  public void runJobFail() throws SettingsException {
    boolean runnedCorrectly = pdiService.runJob(streamWithInValidJob);
    assertFalse(runnedCorrectly);
  }
}
