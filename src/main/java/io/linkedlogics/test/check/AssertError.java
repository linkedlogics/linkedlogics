package io.linkedlogics.test.check;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow.Type;
import io.linkedlogics.model.process.helper.LogicPositioner;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertError {
	private Context context;
	private String id;
	
	public void isRetried() {
		Boolean result = AssertUtil.getSetByType(context, Type.RETRY).contains(id);
		assert result : String.format("Error on %s not retried but expected", id);
	}
	
	public void isNotRetried() {
		Boolean result = AssertUtil.getSetByType(context, Type.RETRY).contains(id);
		assert !result : String.format("Error on %s retried but NOT expected", id);
	}
	
	public void isRetried(int times) {
		int retries = AssertUtil.countResults(context, Type.RETRY, id, true);
		assert retries == times : String.format("Error on %s retried %d times but expected was %d times", id, retries, times);
	}
	
	public void isRetriedAtMost(int times) {
		int retries = AssertUtil.countResults(context, Type.RETRY, id, true);
		assert retries <= times : String.format("Error on %s retried %d times but expected was at most %d", id, retries, times);
	}
	
	public void isRetriedAtLeast(int times) {
		int retries = AssertUtil.countResults(context, Type.RETRY, id, true);
		assert retries >= times : String.format("Error on %s retried %d times but expected was at least %d ", id, retries, times);
	}
	
	public void isCompensated() {
		Boolean result = AssertUtil.getSetByType(context, Type.COMPENSATE).contains(id + LogicPositioner.COMPENSATE);
		assert result : String.format("Error on %s not compensated but expected", id);
	}
	
	public void isNotCompensated() {
		Boolean result = AssertUtil.getSetByType(context, Type.COMPENSATE).contains(id + LogicPositioner.COMPENSATE);
		assert !result : String.format("Error on %s compensated but NOT expected", id);
	}
}
