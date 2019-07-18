package com.openmrs.migrator.unit.core.services;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PDIServiceTest {

  @Autowired private PDIService pdiService;

  @Autowired private FileIOUtilities fileIOUtilities;

  private String[] jobs = {"pdiresources/jobs/job.kjb", "pdiresources/jobs/job-invalid.kjb"};

  private InputStream streamWithValidJob, streamWithInValidJob;

  @Before
  public void setUp() throws Exception {
    streamWithValidJob = fileIOUtilities.getResourceAsStream(jobs[0]);
    streamWithInValidJob = fileIOUtilities.getResourceAsStream(jobs[1]);
  }

  @Test
  public void runJobSuccess() throws KettleException {
    boolean ranCorrectly = pdiService.runJob(streamWithValidJob);
    assertTrue(ranCorrectly);
  }

  @Test
  public void runJobFail() throws KettleException {
    boolean ranCorrectly = pdiService.runJob(streamWithInValidJob);
    assertFalse(ranCorrectly);
  }
}
