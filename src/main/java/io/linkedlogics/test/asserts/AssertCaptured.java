package io.linkedlogics.test.asserts;

import java.util.Map;

import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.test.service.TestLogicService;

public class AssertCaptured {
	private String capturedAs;
	
	public AssertCaptured(String capturedAs) {
		this.capturedAs = capturedAs;
	}
	
	public void when(String expression) {
		TestLogicService logicService = (TestLogicService) ServiceLocator.getInstance().getLogicService();
		Map<String, Object> input =logicService.getCaptured(capturedAs).orElseThrow(() -> new IllegalArgumentException(capturedAs + " is not captured"));
		assert (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression, input) : expression + " has failed with inputs \n " + input.toString();
	}
}
