package dev.linkedlogics.service.handler.logic;

import java.lang.reflect.InvocationTargetException;

import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.context.LogicContext;

public class ErrorHandler extends LogicHandler {
	
	public ErrorHandler() {

	}
	
	public ErrorHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handleError(LogicContext context, Throwable error) {
		error.printStackTrace();
		if (error instanceof InvocationTargetException) {
			error = ((InvocationTargetException) error).getCause();
		}
		context.setError(ContextError.of(error));
		
		super.handleError(context, error);
	}
}
