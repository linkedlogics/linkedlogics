package io.linkedlogics.test.check;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertFail {
	private Context context;
	private String id;
	
	public void isExecuted() {
		assert AssertUtil.getResultOrDefault(context, ContextFlow.Type.FAIL, id, Boolean.FALSE) : String.format("Fail %s is NOT executed but expected", id);
	}
	
	public void isNotExecuted() {
		assert !AssertUtil.getResultOrDefault(context, ContextFlow.Type.FAIL, id, Boolean.FALSE) : String.format("Fail %s is executed but NOT expected", id);
	}
}