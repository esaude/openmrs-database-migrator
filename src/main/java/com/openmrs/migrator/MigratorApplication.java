package com.openmrs.migrator;

import com.openmrs.migrator.core.services.MergeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MigratorApplication implements CommandLineRunner {

  private static Logger LOG = LoggerFactory.getLogger(MigratorApplication.class);

  private final MergeService mergeService;

  @Autowired
  public MigratorApplication(MergeService mergeService) {
    this.mergeService = mergeService;
  }

  public static void main(String[] args) {
    SpringApplication.run(MigratorApplication.class, args);
  }

  @Override
  public void run(String... args) {
    LOG.info("EXECUTING : command line runner");

    for (int i = 0; i < args.length; ++i) {
      LOG.info("args[{}]: {}", i, args[i]);
    }

    if (args.length > 0 && "run".equals(args[0])) {
      mergeService.mergeOpenMRS();
    } else {
      System.out.println("Usage: migrator run");
    }
  }
}
