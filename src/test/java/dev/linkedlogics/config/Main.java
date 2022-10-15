package dev.linkedlogics.config;

import dev.linkedlogics.process.SimpleProcess5Tests;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler;

public class Main {
	public static void main(String[] args) {
		ProcessFlowHandler.LOG_ENTER = true;
		ProcessFlowHandler.LOG_EXIT = true;
		SimpleProcess5Tests t = new SimpleProcess5Tests();
		t.setUp();
//		t.resetCounter();
		t.testScenario1();
		
	}
}
