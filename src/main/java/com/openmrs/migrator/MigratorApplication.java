package com.openmrs.migrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// import java.util.logging.Logger;
// import java.util.logging.LoggerFactory;

@SpringBootApplication
public class MigratorApplication {

  // private static Logger logger = LoggerFactory.getLogger(MigratorApplication.class);

  public static void main(String[] args) {
    // logger.info("STARTING THE MIGRATOR...");
    SpringApplication.run(MigratorApplication.class, args);
    // logger.info("SETUP COMPLETE...");
  }
}
