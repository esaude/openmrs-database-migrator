package com.openmrs.migrator.core.services;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface SettingService {
	
	int createDirectoryStructure(List<String> dirList,Path  settingsProperties) throws IOException;

}
