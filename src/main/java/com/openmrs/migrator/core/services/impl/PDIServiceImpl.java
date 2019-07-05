package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.PDIService;
import java.io.InputStream;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.Result;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.logging.LogLevel;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PDIServiceImpl implements PDIService {

  private static Logger LOG = LoggerFactory.getLogger(PDIServiceImpl.class);

  @Override
  public void runTransformation(InputStream xmlStream) throws KettleException {

    KettleEnvironment.init();

    TransMeta transMeta = new TransMeta(xmlStream, null, true, null, null);

    Trans transformation = new Trans(transMeta);
    transformation.setLogLevel(LogLevel.BASIC);

    String name = transformation.getName();
    LOG.info("Starting transformation " + name);

    transformation.execute(new String[0]);
    transformation.waitUntilFinished();

    Result result = transformation.getResult();

    String outcome =
        String.format(
            "Transformation %s executed %s",
            name,
            (result.getNrErrors() == 0
                ? "successfully"
                : "with " + result.getNrErrors() + " errors"));

    LOG.info(outcome);
  }
}