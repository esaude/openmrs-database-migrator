package com.openmrs.migrator;

import java.util.concurrent.Callable;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "runx", mixinStandardHelpOptions = true)
public class RunCommand implements Callable<Void> {

  @Parameters(description = "represent config file as parameter")
  private String config = "config";

  @Option(
      names = {"-l", "--list"},
      description = "list content ")
  private boolean list;

  @Override
  public Void call() throws Exception {

    return null;
  }
}
