package io.linkedlogics.test.check;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertGroup {
	private Context context;
	private String id;
	
	public void isExecuted() {
		assert AssertUtil.getResultOrDefault(context, ContextFlow.Type.GROUP, id, Boolean.FALSE) : String.format("Group %s is NOT executed but expected", id);
	}
	
	public void isNotExecuted() {
		assert !AssertUtil.getResultOrDefault(context, ContextFlow.Type.GROUP, id, Boolean.FALSE) : String.format("Group %s is executed but NOT expected", id);
	}
	
	public AssertError onError() {
		return new AssertError(context, id);
	}
	
	public AssertFork onFork() {
		return new AssertFork(context, id);
	}
}