package com.openmrs.migrator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Teste {

  public static void main(String[] args) throws IOException, InterruptedException {
    String line;
    String password = null;
    try (BufferedReader bw =
        new BufferedReader(
            new FileReader(
                "/home/cfaife/jembi_dev/openmrs-database-migrator/settings.properties"))) {

      while ((line = bw.readLine()) != null) {
        if (line.contains("password=")) {
          System.out.println(line.split("=")[1]);
          password = line.split("=")[1];
        }
      }
    }
    System.out.println(password);
  }
}
