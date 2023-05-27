package io.linkedlogics.test.check;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.linkedlogics.context.Context;
import io.linkedlogics.test.TestContextService;

public class AssertContext {
	private Context context;
	private String id = "";
	
	public AssertContext(Context context) {
		this.context = context;
	}
	
	public AssertContext() {
		this.context = TestContextService.getCurrentContext();
	}
	
	public AssertContext(String id) {
		this.context = TestContextService.getCurrentContext();
		this.id = id;
	}
	
	public AssertLogic whenLogic(String id) {
		return new AssertLogic(context, this.id + id);
	}
	
	public AssertBranch whenBranch(String id) {
		return new AssertBranch(context, this.id + id);
	}
	
	public AssertGroup whenGroup(String id) {
		return new AssertGroup(context, this.id + id);
	}

	public AssertScript whenScript(String id) {
		return new AssertScript(context, this.id + id);
	}
	
	public AssertVerify whenVerify(String id) {
		return new AssertVerify(context, this.id + id);
	}
	
	public AssertAny whenAny(String id) {
		return new AssertAny(context, this.id + id);
	}
	
	public AssertFail whenFail(String id) {
		return new AssertFail(context, this.id + id);
	}
	
	public AssertJump whenJump(String id) {
		return new AssertJump(context, this.id + id);
	}
	
	public AssertExit whenExit(String id) {
		return new AssertExit(context, this.id + id);
	}
	
	public AssertLoop whenLoop(String id) {
		return new AssertLoop(context, this.id + id);
	}
	
	public AssertAll whenAll(String... ids) {
		List<String> idList = Arrays.stream(ids).map(i -> this.id + i).collect(Collectors.toList());
		String[] newIds = new String[ids.length];
		idList.toArray(newIds);
		return new AssertAll(context, newIds);
	}
}
