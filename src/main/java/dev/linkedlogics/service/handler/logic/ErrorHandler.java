package dev.linkedlogics.service.handler.logic;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;

public class ErrorHandler extends LogicHandler {
	
	public ErrorHandler() {

	}
	
	public ErrorHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		error.printStackTrace();
		context.setError(ContextError.of(error));
		super.handleError(context, error);
	}
}
