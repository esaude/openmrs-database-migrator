package com.openmrs.migrator.core.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.ConfigurationService;
import com.openmrs.migrator.core.services.DBService;

/**
 * Database operations
 */
@Service
public class DataBaseServiceImpl implements DBService {
	
	@Autowired
	private CommandService commandService;
	
	@Autowired
	private ConfigurationService configurationService;
	
	@Override
	public void importDatabaseFile(String databaseName, String fileName) {
		commandService.runCommand("mysql", "-u" + configurationService.getDatabaseUser(),
		    "-p" + configurationService.getDatabasePassword(), "-h" + configurationService.getDatabasePassword(), "-e",
		    String.format("use %s; source %s;", databaseName, fileName));
	}
	
	@Override
	public void createDatabase(String databaseName) {
		commandService.runCommand("mysql", "-u" + configurationService.getDatabaseUser(),
		    "-p" + configurationService.getDatabasePassword(), "-h" + configurationService.getDatabaseHost(), "-e",
		    String.format("drop database if exists %s; create database %s;", databaseName, databaseName));
	}
	
}
