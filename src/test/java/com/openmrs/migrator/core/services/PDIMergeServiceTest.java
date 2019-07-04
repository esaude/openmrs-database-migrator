package com.openmrs.migrator.core.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

import com.openmrs.migrator.core.services.impl.PDIMergeService;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PDIMergeServiceTest {

  @Autowired PDIMergeService pdiMergeService;

  @MockBean PDIService pdiService;

  @MockBean ResourceLoader resourceLoader;

  @Before
  public void setUp() {
    doNothing().when(pdiService).runTransformation(any(InputStream.class));
    InputStream stream = new ByteArrayInputStream("".getBytes());
    doReturn(stream).when(resourceLoader).getResourceAsStream(any(String.class));
  }

  @Test
  public void mergeOpenMRSShouldRunTransformations() {
    pdiMergeService.mergeOpenMRS();
    verify(pdiService).runTransformation(any(InputStream.class));
  }

  @Test
  public void mergeOpenMRSShouldLoadTransformations() {
    pdiMergeService.mergeOpenMRS();
    verify(resourceLoader).getResourceAsStream(any(String.class));
  }
}
