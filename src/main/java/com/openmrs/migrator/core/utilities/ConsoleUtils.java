package com.openmrs.migrator.core.utilities;

import java.io.Console;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ConsoleUtils {

  private static Console console = System.console();

  public static Optional<String> getDatabaseDetaName() {

    checkConsoleAvailability();
    Optional<String> wrappedName = Optional.empty();

    console.writer().println("Database name:");
    String dataBaseName = console.readLine();

    wrappedName = Optional.of(dataBaseName);

    return wrappedName;
  }

  private static void checkConsoleAvailability() {

    if (console == null) {
      throw new NullPointerException("System.console() is null");
    }
  }

  public static boolean isConnectionIsToBeStored() {
    checkConsoleAvailability();
    console.writer().println("Do you want to keep this connection? [y/n]");
    String answer = console.readLine();
    if ("y".equals(answer)) {
      return true;
    }

    return false;
  }

  public static int startMigrationAproach() {

    checkConsoleAvailability();
    console.writer().println("Below are options to start the  migration tool, select one of them:");
    console.writer().println("1 - Provide a source database name");
    console.writer().println("2 - Use one of the database names in the config file");
    console.writer().println("3 - I want the tool to load the db file");
    int choice;
    try {
      choice = Integer.parseInt(console.readLine());
      return choice;
    } catch (NumberFormatException e) {
      console.writer().println("Select one of the listed options");
      return 0;
    }
  }

  public static String getValidSelectedDataBase(Set<String> dataBases) {

    checkConsoleAvailability();
    if (dataBases.isEmpty()) {
      console.writer().println("There is no valid databases in the config file");
      return null;
    }
    console.writer().println("Valid databases names");
    dataBases.forEach(name -> console.writer().println(name));
    console.writer().println("Above are the valid databases, go ahead with one to migrate");

    console.writer().print("Selected data base name");
    return console.readLine();
  }

  public static void collectMysqlConnection() {
    checkConsoleAvailability();
    console.writer().println("Please  do not forget to fill the setting.properties  file");
  }

  public static void showUnavailableOption() {
    checkConsoleAvailability();
    console.writer().println("Unavailable Option");
  }

  public static Map<String, String> readSourceDBConn() {

    checkConsoleAvailability();
    Map<String, String> dbConn = new HashMap<>();
    console.writer().println("Provide  the source data base connection.");

    console.writer().println("username:");
    dbConn.put("username=", console.readLine());

    console.writer().println("password:");
    dbConn.put("password=", new String(console.readPassword()));

    console.writer().println("host:");
    dbConn.put("host=", console.readLine());

    console.writer().println("port:");
    dbConn.put("port=", console.readLine());

    return dbConn;
  }
}
