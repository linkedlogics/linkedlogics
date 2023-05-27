package io.linkedlogics.test.check;

import io.linkedlogics.context.Context;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AssertAny {
	private Context context;
	private String id;
	
	public void isExecuted() {
		assert AssertUtil.getMap(context).getOrDefault(id, Boolean.FALSE) : String.format("Logic %s is NOT executed but expected", id);
	}
	
	public void isNotExecuted() {
		assert !AssertUtil.getMap(context).getOrDefault(id, Boolean.FALSE) : String.format("Logic %s is executed but NOT expected", id);
	}
}