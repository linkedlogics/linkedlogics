package io.linkedlogics.test.asserts;

import java.util.Map;

import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.test.service.TestLogicService;

public class AssertInputs {
	
	public AssertCaptured capturedAs(String capturedAs) {
		return new AssertCaptured(capturedAs);
	}
}
