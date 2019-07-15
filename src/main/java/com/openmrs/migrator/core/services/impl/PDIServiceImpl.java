package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.PDIService;
import java.io.InputStream;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PDIServiceImpl implements PDIService {

  private static Logger LOG = LoggerFactory.getLogger(PDIServiceImpl.class);

  @Override
  public boolean runJob(InputStream pdiJobFileStream) throws KettleException {

    KettleEnvironment.init();

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

    outcome = String.format("Job %s executed %s", name, "with " + result.getNrErrors() + " errors");
    LOG.info(outcome);

    return executedSucessfully;
  }
}
