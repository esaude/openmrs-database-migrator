package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.MergeService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.io.InputStream;
import org.pentaho.di.core.exception.KettleException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Merge service that uses PDI. */
@Component
public class PDIMergeService implements MergeService {

  private final PDIService pdiService;

  private final FileIOUtilities fileIOUtilities;

  private final String[] transformations = {"merge-patient.ktr"};

  @Autowired
  public PDIMergeService(PDIService pdiService, FileIOUtilities fileIOUtilities) {
    this.pdiService = pdiService;
    this.fileIOUtilities = fileIOUtilities;
  }

  @Override
  public void mergeOpenMRS() {
    try {
      for (String t : transformations) {
        InputStream xml = fileIOUtilities.getResourceAsStream(t);
        pdiService.runTransformation(xml);
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (KettleException e) {
      // Do nothing kettle prints stack trace
    }
  }
}
