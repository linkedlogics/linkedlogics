package io.linkedlogics.test.check;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertJump {
	private Context context;
	private String id;
	
	public void isExecuted() {
		assert AssertUtil.getResultOrDefault(context, ContextFlow.Type.JUMP, id, Boolean.FALSE) : String.format("%s is NOT jumped but expected", id);
	}
	
	public void isNotExecuted() {
		assert !AssertUtil.getResultOrDefault(context, ContextFlow.Type.JUMP, id, Boolean.FALSE) : String.format("%s is jumped but NOT expected", id);
	}
}