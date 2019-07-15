package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.io.InputStream;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.job.Job;
import org.pentaho.di.job.JobMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PDIServiceImpl implements PDIService {

  private static Logger LOG = LoggerFactory.getLogger(PDIServiceImpl.class);

  @Autowired private FileIOUtilities fileIOUtilities;

  @Override
  public boolean runJob(InputStream xmlStream) throws KettleException {

    KettleEnvironment.init();

    JobMeta jobMeta = new JobMeta(xmlStream, null, null);

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

  @Override
  public void mergeOpenMRS(String[] jobs) throws IOException {
    try {
      for (String t : jobs) {

        InputStream xml = fileIOUtilities.getResourceAsStream(t);
        runJob(xml);
      }
    } catch (KettleException e) {
      // Do nothing kettle prints stack trace
    }
  }
}
