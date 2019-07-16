package com.openmrs.migrator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Teste {

	public static void main(String[] args) {
 
		 List<String> fileList = Arrays.asList("input/", "output/", "config/", "pdiresources/");

		    fileList.forEach(
		        x -> {
		          try {
		            Files.createDirectory(Paths.get(x));
		          } catch (IOException e) {
		            e.printStackTrace();
		          }
		        });
	}

}
