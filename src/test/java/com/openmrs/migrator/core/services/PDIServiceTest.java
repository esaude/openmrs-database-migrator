package com.openmrs.migrator.core.services;

import static org.junit.Assert.assertTrue;

import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
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

  private String[] jobs = {"pdiresources/jobs/merge-patient-job.kjb"};

  InputStream stream;

  @Before
  public void setUp() throws Exception {
    stream = fileIOUtilities.getResourceAsStream(jobs[0]);
  }

  @Test
  public void testRunJob() throws KettleException {
    boolean runnedCorrectly = pdiService.runJob(stream);
    assertTrue(runnedCorrectly);
  }

  @Test(expected = IOException.class)
  public void testMergeOpenMRS() throws IOException {

    pdiService.mergeOpenMRS(new String[] {"fake/path"});
  }
}
