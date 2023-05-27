package io.linkedlogics.test.check;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertBranch {
	private Context context;
	private String id;
	
	public void isSatisfied() {
		Boolean result = AssertUtil.getResult(context, ContextFlow.Type.BRANCH, id);
		assert result != null : String.format("Branch %s is NOT executed but expected", id);
		assert result : String.format("Branch %s is NOT satisfied but expected", id);
	}
	
	public void isNotSatisfied() {
		Boolean result = AssertUtil.getResult(context, ContextFlow.Type.BRANCH, id);
		assert result != null : String.format("Branch %s is NOT executed but expected", id);
		assert !result : String.format("Branch %s is satisfied but NOT expected", id);
	}
}
