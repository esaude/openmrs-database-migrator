package com.openmrs.migrator.core.utilities;

import com.openmrs.migrator.core.services.SettingsService;
import java.io.Console;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.apache.commons.lang.StringUtils;

public class ConsoleUtils {

  public static Optional<String> getDatabaseDetaName(Console console) {

    checkConsoleAvailability(console);
    Optional<String> wrappedName = Optional.empty();

    console.writer().println("Database name:");
    String dataBaseName = console.readLine();

    wrappedName = Optional.of(dataBaseName);

    return wrappedName;
  }

  private static void checkConsoleAvailability(Console console) {

    if (console == null) {
      throw new IllegalArgumentException("System.console() can't be  null");
    }
  }

  public static boolean isConnectionIsToBeStored(Console console) {
    checkConsoleAvailability(console);
    console.writer().println("Do you want to keep this connection? [y/n]");
    String answer = console.readLine();
    return "y".equals(answer);
  }

  public static int startMigrationAproach(Console console) {

    checkConsoleAvailability(console);
    console.writer().println("Below are the options for database source:");
    console.writer().println("1 - Provide source database name for existing db in MySQL server");
    console.writer().println("2 - Load sql dump files");
    int choice;
    try {
      choice = Integer.parseInt(console.readLine());
      return choice;
    } catch (NumberFormatException e) {
      console.writer().println("Select one of the listed options");
      return 0;
    }
  }

  public static String getValidSelectedDataBase(Console console, Set<String> dataBases) {

    checkConsoleAvailability(console);
    if (dataBases.isEmpty()) {
      console.writer().println("There is no valid databases in the config file");
      return null;
    }
    console.writer().println("Valid databases names:");
    dataBases.forEach(name -> console.writer().println(name));
    console.writer().println("Above are the valid databases, go ahead with one to migrate:");

    console.writer().print("Selected data base name");
    return console.readLine();
  }

  public static void showUnavailableOption(Console console) {
    checkConsoleAvailability(console);
    console.writer().println("Unavailable Option");
  }

  public static Map<String, String> readSettingsFromConsole(Console console) {
    checkConsoleAvailability(console);

    Map<String, String> dbConn = new HashMap<>();

    console.writer().println("Test Database Connection First: y/n:");
    dbConn.put(
        SettingsService.DB_TEST_CONNECTION,
        "y".equalsIgnoreCase(console.readLine()) ? "true" : "false");

    console.writer().println("Folder location containing backups: input/");
    String dbsLocation = console.readLine();
    dbConn.put(
        SettingsService.DBS_BACKUPS_DIRECTORY,
        StringUtils.isBlank(dbsLocation) ? "input/" : dbsLocation);

    console.writer().println("Provide  the source data base connection.");

    console.writer().println("username:");
    dbConn.put(SettingsService.DB_USER, console.readLine());

    console.writer().println("password:");
    dbConn.put(SettingsService.DB_PASS, new String(console.readPassword()));

    console.writer().println("host:");
    dbConn.put(SettingsService.DB_HOST, console.readLine());

    console.writer().println("port:");
    dbConn.put(SettingsService.DB_PORT, console.readLine());

    return dbConn;
  }

  public static String chooseDumpFile(Console console, List<Path> inputs) {

    checkConsoleAvailability(console);
    console.writer().println("Choose a dump file from the list to restore your mysql instance");
    if (inputs.isEmpty()) {
      console.writer().println("There is no  input files");
      return null;
    }
    inputs.forEach(input -> console.writer().println(input));
    console.writer().println("Dump file:");
    return console.readLine();
  }

  public static String getChosenDBName(Console console) {
    checkConsoleAvailability(console);
    console.writer().println("Data Base name:");
    return console.readLine();
  }
}
