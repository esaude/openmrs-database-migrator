package com.openmrs.migrator.core.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.openmrs.migrator.core.services.CommandService;
import com.openmrs.migrator.core.services.DBService;

@Service
public class DBServiceImpl implements DBService {
	
	@Autowired
	private CommandService commandService;
	
	@Override
	public void importDatabaseFile(String databaseName, String fileName) {
		commandService.runCommand("mysql", "-u" + getDatabaseUser(), "-p" + getDatabasePassword(),
		    "-h" + getDatabasePassword(), "-e", String.format("use %s; source %s;", databaseName, fileName));
	}
	
	@Override
	public void createDatabase(String databaseName) {
		commandService.runCommand("mysql", "-u" + getDatabaseUser(), "-p" + getDatabasePassword(), "-h" + getDatabaseHost(),
		    "-e", String.format("drop database if exists %s; create database %s;", databaseName, databaseName));
	}
	
	private String getDatabaseHost() {
		//TODO: must come from ConfigurationService
		return "localhost";
	}
	
	private String getDatabasePassword() {
		//TODO: must come from ConfigurationService
		return "Admin123";
	}
	
	private String getDatabaseUser() {
		//TODO: must come from ConfigurationService
		return "etl";
	}
	
}
