package dev.linkedlogics.exception;

import dev.linkedlogics.context.ContextError.ErrorType;

@SuppressWarnings("serial")
public class CycleDetectedError extends LogicException {
	
	public CycleDetectedError(String id, int version) {
		super(-1, String.format("cycle detected for %s[%d]", id, version), ErrorType.PERMANENT);
	}
}

