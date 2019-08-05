package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.exceptions.SettingsException;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.SettingsService;
import java.io.InputStream;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PDIServiceImpl implements PDIService {
  @Autowired private SettingsService settingsService;

  private static Logger LOG = LoggerFactory.getLogger(PDIServiceImpl.class);

  @Override
  public boolean runJob(InputStream pdiJobFileStream) throws SettingsException {
    try {
      // TODO somehow i think this is better in MigratorApplication run
      settingsService.initializeKettleEnvironment(true);

      JobMeta jobMeta = new JobMeta(pdiJobFileStream, null, null);

      Job job = new Job(null, jobMeta);
      job.setLogLevel(LogLevel.BASIC);

      String name = job.getName();
      LOG.info("Starting Job " + name);

      Result result = job.execute(1, null);

      job.waitUntilFinished();

      boolean executedSucessfully = false;

      String outcome;

      if (result.getNrErrors() == 0) {
        outcome = String.format("Job %s executed %s", name, "successfully");

        executedSucessfully = true;
        LOG.info(outcome);
        return executedSucessfully;
      }

      outcome =
          String.format("Job %s executed %s", name, "with " + result.getNrErrors() + " errors");
      LOG.error(outcome);

      return executedSucessfully;
    } catch (Exception e) {
      throw new SettingsException(e);
    }
  }
}
