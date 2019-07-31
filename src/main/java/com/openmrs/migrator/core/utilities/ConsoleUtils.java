package com.openmrs.migrator.core.utilities;

import java.io.Console;
import java.util.Optional;

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
	    console.writer().println("Below  are options to start the  migration tool, select one of them:");
	    console.writer().println("1 - Provide a source database name");
	    console.writer().println("2 - Use one of the database names in the config file");
	    console.writer().println("3 - I want the tool to load the db file");
	    int choice;
	   try {
		   choice= Integer.parseInt(console.readLine());
		   return choice;
	   }catch (NumberFormatException  e) {
		   console.writer().println("Select one of the listed options");
		return 0;
	}
	  
	  
  }
}
