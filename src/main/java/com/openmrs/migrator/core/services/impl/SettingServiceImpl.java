package com.openmrs.migrator.core.services.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.openmrs.migrator.core.services.SettingService;

@Service
public class SettingServiceImpl implements SettingService {
	
	private static Logger log = LoggerFactory.getLogger(SettingServiceImpl.class);

	
	int numberOfCreatedFiles =0;
	
	/**
	 * Creates folders structure, if a one of the folder exists a warn  log is raised 
	 * informing that the  folder or  won't be created because  it already exists
	 * 
	 */
	@Override
	public int createDirectoryStructure(List<String> dirList,Path  settingsProperties) throws IOException {

		log.info("Starting creating folder structure");
		
		dirList.forEach(x -> {
			if (!checkIfPathExist(Paths.get(x))) {
				try {
					Files.createDirectory(Paths.get(x));
					log.info("Folder: "+x+" created with sucess");
					numberOfCreatedFiles++;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				log.warn("Folder: "+x+" will not be created, folder already exists");
			}
		});
	 

		
		if (!checkIfPathExist(settingsProperties)) {
			 Files.createFile(settingsProperties);
			 numberOfCreatedFiles++;
			 log.info("File: settings.properties created with sucess");
		}else {
			log.warn ("File: settings.properties will not be created file already exists");
		}
		
		return numberOfCreatedFiles;

	}

	private boolean checkIfPathExist(Path path) {

		return Files.exists(path);

	}

}
