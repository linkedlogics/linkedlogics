package io.linkedlogics.test.check;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertExit {
	private Context context;
	private String id;
	
	public void isExecuted() {
		assert AssertUtil.getResultOrDefault(context, ContextFlow.Type.EXIT, id, Boolean.FALSE) : String.format("Exit %s is NOT executed but expected", id);
	}
	
	public void isNotExecuted() {
		assert !AssertUtil.getResultOrDefault(context, ContextFlow.Type.EXIT, id, Boolean.FALSE) : String.format("Exit %s is executed but NOT expected", id);
	}
}