package dev.linkedlogics.config;

import dev.linkedlogics.process.CompensateProcess1Tests;
import dev.linkedlogics.process.ErrorProcess2Tests;
import dev.linkedlogics.process.ForkJoinProcess1Tests;
import dev.linkedlogics.process.SimpleProcess1Tests;
import dev.linkedlogics.process.SimpleProcess3Tests;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler;

public class Main {
	public static void main(String[] args) {
		ProcessFlowHandler.LOG_ENTER = true;
		ProcessFlowHandler.LOG_EXIT = true;
		SimpleProcess1Tests t = new SimpleProcess1Tests();
		t.setUp();
//		t.resetCounter();
		t.testScenario7();
	}
}
