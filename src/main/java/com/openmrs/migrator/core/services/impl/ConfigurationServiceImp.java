package com.openmrs.migrator.core.services.impl;

import org.springframework.stereotype.Service;

import com.openmrs.migrator.core.services.ConfigurationService;

/**
 * Access to all system configuration
 */
@Service
public class ConfigurationServiceImp implements ConfigurationService {
	
	@Override
	public String getDatabaseHost() {
		return "localhost";
	}
	
	@Override
	public String getDatabasePassword() {
		return "Admin123";
	}
	
	@Override
	public String getDatabaseUser() {
		return "etl";
	}
	
}
