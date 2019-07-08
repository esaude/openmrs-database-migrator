package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.exceptions.CommandExecutionException;
import com.openmrs.migrator.core.services.CommandService;
import java.io.IOException;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** Helps issuing operating system commands */
@Service
public class CommandServiceImpl implements CommandService {

  private static Logger LOG = LoggerFactory.getLogger(CommandServiceImpl.class);

  @Override
  public void runCommand(String... args) {
    logCommand(args);
    try {
      ProcessBuilder pb = new ProcessBuilder(args);
      Process process = pb.start();
      int executionResult = process.waitFor();
      if (executionResult != 0) {
        throw new CommandExecutionException(
            String.format("Command terminated with errors: %s", executionResult));
      }
    } catch (IOException | InterruptedException e) {
      throw new CommandExecutionException(e);
    }
  }

  private void logCommand(String... args) {
    StringBuffer sb = new StringBuffer();
    Arrays.asList(args).forEach(s -> sb.append(s + " "));
    LOG.info(String.format("Running command: %s", sb.toString()));
  }
}
