package com.openmrs.migrator.core.config;

import com.openmrs.migrator.model.DataBaseConnectionDetail;
import java.io.Console;
import java.util.Optional;
import picocli.CommandLine.Command;

@Command(name = "run")
public class RunCommandHandler {

  private Console console = System.console();

  @Command(name = "")
  Optional<DataBaseConnectionDetail> getDatabaseDetail() {

    Optional<DataBaseConnectionDetail> wrappedDatabaseDetail = Optional.empty();
    if (console == null) {
      throw new NullPointerException("System.console() is null");
    }

    console.writer().println("Which database do you want to execute the migration?");
    String name = console.readLine();

    console.writer().println("Database username:");
    String username = console.readLine();

    console.writer().println("Database password:");
    char[] password = console.readPassword();

    wrappedDatabaseDetail =
        Optional.of(new DataBaseConnectionDetail(name, username, password.toString()));

    return wrappedDatabaseDetail;
  }
}
