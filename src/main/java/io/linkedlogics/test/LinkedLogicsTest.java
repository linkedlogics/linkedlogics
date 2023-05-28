package io.linkedlogics.test;

import io.linkedlogics.context.Context;
import io.linkedlogics.test.asserts.AssertContext;

public class LinkedLogicsTest {
	public static void mock(String logicId) {
		
	}
	
	public static void mock(String logicId, int logicVersion) {
		
	}
	
	public static AssertContext assertContext() {
		return new AssertContext();
	}
	
	public static AssertContext assertContext(Context context) {
		return new AssertContext(context);
	}
}
