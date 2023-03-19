package dev.linkedlogics.service.handler.logic;

import dev.linkedlogics.context.Context;

public class LoggerHandler extends LogicHandler {

	public LoggerHandler() {

	}
	
	public LoggerHandler(LogicHandler handler) {
		super(handler);
	}
	
	@Override
	public void handle(Context context, Object result) {
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		super.handleError(context, error);
	}
}
