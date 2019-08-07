package com.openmrs.migrator.core.services.impl;

import com.openmrs.migrator.core.services.SettingsService;
import com.openmrs.migrator.core.utilities.FileIOUtilities;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SettingsServiceImpl implements SettingsService {

  private static Logger logger = LoggerFactory.getLogger(SettingsServiceImpl.class);

  @Autowired private FileIOUtilities fileIOUtilities;

  public void fillConfigFile(Path target, Map<String, String> connDB) throws IOException {

    fileIOUtilities.writeToFile(
        target.toFile(),
        SettingsService.DB_USER + "=" + connDB.get(SettingsService.DB_USER),
        SettingsService.DB_PASS + "=" + connDB.get(SettingsService.DB_PASS),
        SettingsService.DB_HOST + "=" + connDB.get(SettingsService.DB_HOST),
        SettingsService.DB_PORT + "=" + connDB.get(SettingsService.DB_PORT));
  }

  public void addSettingToConfigFile(Path target, String labelName, String configVaule)
      throws IOException {
    logger.info("Adding " + labelName + ":" + configVaule + " in config file");

    List<String> lines = Files.readAllLines(target);
    lines.add(labelName + "=" + configVaule);
    Files.write(target, lines, StandardCharsets.UTF_8);
  }
}
