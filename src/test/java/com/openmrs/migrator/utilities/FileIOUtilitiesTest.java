package com.openmrs.migrator.utilities;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.openmrs.migrator.core.utilities.FileIOUtilities;

@RunWith(SpringRunner.class)
@SpringBootTest
public class FileIOUtilitiesTest {
	
	  @Autowired private FileIOUtilities fileIOUtilities;
	  
	  private String stream = "pdiresources/jobs/merge-patient-job.kjb";
	  
	  
	  @Test 
	  public void getValidResourceAsStream() throws IOException {

		  InputStream inputStream =  fileIOUtilities.getResourceAsStream(stream);
		  assertNotNull(inputStream);
	  }
	  
	  @Test(expected = IOException.class)
	  public void getInvalidResourceAsStream() throws IOException {

		  fileIOUtilities.getResourceAsStream("fake/path");
	  }


}