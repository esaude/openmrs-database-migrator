package com.openmrs.migrator.core.services;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BootstrapServiceTest {

  @Autowired private BootstrapService bootstrapService;

  private List<String> folders = Arrays.asList("folder0", "folder1", "folder2");

  private Path file = Paths.get("file.unknown");

  @Test
  public void createDirectoryStructureSuccess() throws IOException {

    removeFolders();

    int numberOfCreatrions = bootstrapService.createDirectoryStructure(folders, file);
    assertEquals(4, numberOfCreatrions);
    removeFolders();
    assertTrue(Files.notExists(Paths.get("folder0")));
    assertTrue(Files.notExists(Paths.get("folder1")));
    assertTrue(Files.notExists(Paths.get("folder2")));
    assertTrue(Files.notExists(file));
  }

  @Test
  public void populateDefaultResoucesSuccess() throws IOException {
	  
	  Files.deleteIfExists(Paths.get("populatedFile.c"));

	  Set<String> fileNames = new HashSet<>();
	  
	  fileNames.add("populatedFile.c");
	  
	  Set<String> createdFiles =   bootstrapService.populateDefaultResource(fileNames);
	  assertTrue(createdFiles.contains("populatedFile.c"));;
	  
	  Files.deleteIfExists(Paths.get("populatedFile.c"));
	   
	   
  }

  private void removeFolders() throws IOException {
    folders.forEach(
        x -> {
          try {
            Files.deleteIfExists(Paths.get(x));
          } catch (IOException e) {

          }
        });
    Files.deleteIfExists(file);
  }
}
