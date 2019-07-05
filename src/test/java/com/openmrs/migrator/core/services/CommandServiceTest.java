package com.openmrs.migrator.core.services;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.openmrs.migrator.core.exception.CommandExecutionException;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CommandServiceTest {
	
	@Autowired
	private CommandService commandService;
	
	@Test
	public void shouldExecuteLsCommand() {
		commandService.runCommand("ls", "-ltr");
	}
	
	@Test(expected = CommandExecutionException.class)
	public void shouldRaiseExceptionIfInvalidCommand() {
		commandService.runCommand("unexistingcommand");
	}
}
