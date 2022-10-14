package dev.linkedlogics.config;

import dev.linkedlogics.process.DelayedProcessor1Tests;
import dev.linkedlogics.process.RetryProcess1Tests;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler;

public class Main {
	public static void main(String[] args) {
		ProcessFlowHandler.LOG_ENTER = true;
		ProcessFlowHandler.LOG_EXIT = true;
		RetryProcess1Tests t = new RetryProcess1Tests();
		t.setUp();
		t.resetCounter();
		t.testScenario1();
		
	}
}
