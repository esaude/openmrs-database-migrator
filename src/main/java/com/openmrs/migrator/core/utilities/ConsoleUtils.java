package com.openmrs.migrator.core.utilities;

import com.openmrs.migrator.core.services.SettingsService;
import java.io.Console;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;

public class ConsoleUtils {

  public static Optional<String> getDatabaseName(
      Console console, List<String> alreadyLoadedDataBases, String labelPrefix) {

    checkConsoleAvailability(console);
    Optional<String> wrappedName = Optional.empty();

    String existingDbNames = String.join("/", alreadyLoadedDataBases);
    console.writer().println(labelPrefix + " Database name: (" + existingDbNames + ")");
    String dataBaseName = console.readLine();
    while (!alreadyLoadedDataBases.contains(dataBaseName)) {
      console.writer().println("Choose from existing databases please! (" + existingDbNames + ")");
      dataBaseName = console.readLine();
    }

    wrappedName = Optional.of(dataBaseName);

    return wrappedName;
  }

  private static void checkConsoleAvailability(Console console) {

    if (console == null) {
      throw new IllegalArgumentException("System.console() can't be  null");
    }
  }

  public static int startMigrationAproach(Console console) {

    checkConsoleAvailability(console);
    console
        .writer()
        .println("1 - Provide source and merge database name for existing db in MySQL Server");
    console.writer().println("2 - Load sql dump files into MySQL Server");
    int choice;
    try {
      choice = Integer.parseInt(console.readLine());
      return choice;
    } catch (NumberFormatException e) {
      console.writer().println("Select one of the listed options");
      return 0;
    }
  }

  public static void showUnavailableOption(Console console) {
    checkConsoleAvailability(console);
    console.writer().println("Unavailable Option");
  }

  public static Map<String, String> readSettingsFromConsole(Console console) {
    checkConsoleAvailability(console);

    Map<String, String> dbConn = new HashMap<>();

    dbConn.put(SettingsService.DB_TEST_CONNECTION, "true");

    console.writer().println("Provide  the source data base connection.");

    console.writer().println("username:");
    dbConn.put(SettingsService.DB_USER, console.readLine());

    console.writer().println("password:");
    dbConn.put(SettingsService.DB_PASS, new String(console.readPassword()));

    console.writer().println("host: localhost");
    String host = console.readLine();
    dbConn.put(SettingsService.DB_HOST, StringUtils.isBlank(host) ? "localhost" : host);

    console.writer().println("port:");
    dbConn.put(SettingsService.DB_PORT, console.readLine());

    return dbConn;
  }

  public static String readFromConsole(String label, Console console) {
    console.writer().println(label);
    return console.readLine();
  }

  public static String chooseDumpFile(
      Console console, List<Path> inputs, String locationOfBackups) {

    checkConsoleAvailability(console);
    console.writer().println("Choose a dump file from the list to restore your mysql instance");
    if (inputs.isEmpty()) {
      console.writer().println("There is no  input files in: " + locationOfBackups);
      return null;
    }
    inputs.forEach(input -> console.writer().println(input));
    console.writer().println("Dump file:");
    return console.readLine();
  }
}
