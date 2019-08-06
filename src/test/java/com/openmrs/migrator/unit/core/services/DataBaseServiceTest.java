package com.openmrs.migrator.unit.core.services;

import com.openmrs.migrator.core.services.DataBaseService;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DataBaseServiceTest {

  @Autowired private DataBaseService dataBaseService;

  @Test
  public void getDatabasesNameSholdReturnAtLeastName() throws IOException {
    List<String> names = dataBaseService.runSQLCommand("username", "password", "show databases");
    Assert.assertFalse(names.isEmpty());
    Assert.assertEquals("", names.get(0));
  }

  @Test
  public void shouldFindOneValidDataBasefromConfigFile() throws FileNotFoundException, IOException {
    List<String> fromConfig = new ArrayList<>();
    List<String> fromMySql = new ArrayList<>();
    Set<String> validNames = dataBaseService.validateDataBaseNames(fromConfig, fromMySql);
    Assert.assertTrue(validNames.isEmpty());
  }
}
