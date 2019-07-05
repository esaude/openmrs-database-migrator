package com.openmrs.migrator.core.services.impl;

import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.openmrs.migrator.core.services.CommandService;

import exception.CommandExecutionException;

@Service
public class ComandServiceImpl implements CommandService {
	
	private static Logger LOG = LoggerFactory.getLogger(ComandServiceImpl.class);
	
	@Override
	public void runCommand(String... args) {
		logCommand(args);
		try {
			ProcessBuilder pb = new ProcessBuilder(args);
			pb.redirectOutput(Redirect.INHERIT);
			pb.redirectError(Redirect.INHERIT);
			Process process = pb.start();
			int executionResult = process.waitFor();
			if (executionResult != 0) {
				throw new CommandExecutionException(String.format("Command terminated with errors: %s", executionResult));
			}
		}
		catch (IOException | InterruptedException e) {
			throw new CommandExecutionException(e);
		}
	}
	
	private void logCommand(String... args) {
		StringBuffer sb = new StringBuffer();
		Arrays.asList(args).forEach(s -> sb.append(s + " "));
		LOG.info(String.format("Running command: %s", sb.toString()));
	}
	
}
