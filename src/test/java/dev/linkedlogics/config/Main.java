package dev.linkedlogics.config;

import dev.linkedlogics.process.ErrorProcess3Tests;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler;

public class Main {
	public static void main(String[] args) {
		ProcessFlowHandler.LOG_ENTER = true;
		ProcessFlowHandler.LOG_EXIT = true;
		ErrorProcess3Tests t = new ErrorProcess3Tests();
		t.setUp();
//		t.resetCounter();
		t.testScenario3();
	}
}
