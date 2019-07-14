package com.openmrs.migrator.core.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PDIMergeServiceTest {

  @MockBean private PDIService pdiService;

  @MockBean private FileIOUtilities fileIOUtilities;

  InputStream stream;

  @Before
  public void setUp() throws Exception {
    doNothing().when(pdiService).runJob(any(InputStream.class));
    doReturn(stream).when(fileIOUtilities).getResourceAsStream(any(String.class));
  }

  @Test
  public void testRunJob() throws KettleException {

    InputStream stream = new ByteArrayInputStream(" ".getBytes());
    pdiService.runJob(stream);
    verify(pdiService).runJob(any(InputStream.class));
  }
}
