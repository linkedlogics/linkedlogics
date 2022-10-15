package dev.linkedlogics.exception;

import dev.linkedlogics.context.ContextError.ErrorType;

@SuppressWarnings("serial")
public class MissingLogicError extends LogicException {
	
	public MissingLogicError(String id, int version) {
		super(-1, String.format("missing %s[%d]", id, version), ErrorType.PERMANENT);
	}
}

