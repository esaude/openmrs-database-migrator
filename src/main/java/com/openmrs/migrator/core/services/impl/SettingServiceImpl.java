package com.openmrs.migrator.core.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
 
import org.springframework.stereotype.Service;

import com.openmrs.migrator.core.services.SettingService;

@Service
public class SettingServiceImpl implements SettingService{

	@Override
	public void settupFoldersStructure() {
		
		 List<String> fileList = Arrays.asList("input/",
				 								"output/",
				 								"config/",
				 								"pdiresources/"
				 								);
		 
		 fileList.forEach(x->{
			 try {
				Files.createDirectory(Paths.get(x));
			} catch (IOException e) {
 				e.printStackTrace();
			}
		 });
		
		 //Files.createFile("settings.properties");
		 
		
		
		 
		
	}
	
	

}
