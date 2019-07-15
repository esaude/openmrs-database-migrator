package com.openmrs.migrator.utilities;

import java.io.IOException;

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
	  
	  
	  @Test(expected = IOException.class)
	  public void getResourceAsStream() throws IOException {

		  fileIOUtilities.getResourceAsStream("fake/path");
	  }


}
