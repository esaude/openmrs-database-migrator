package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.MergeService;
import com.openmrs.migrator.core.services.PDIService;
import com.openmrs.migrator.core.services.ResourceLoader;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/** Merge service that uses PDI. */
@Component
public class PDIMergeService implements MergeService {

  private final PDIService pdiService;

  private final ResourceLoader resourceLoader;

  private final String[] transformations = {"merge-patient.ktr"};

  @Autowired
  public PDIMergeService(PDIService pdiService, ResourceLoader resourceLoader) {
    this.pdiService = pdiService;
    this.resourceLoader = resourceLoader;
  }

  @Override
  public void mergeOpenMRS() {
    for (String t : transformations) {
      InputStream xml = resourceLoader.getResourceAsStream(t);
      pdiService.runTransformation(xml);
    }
  }
}
