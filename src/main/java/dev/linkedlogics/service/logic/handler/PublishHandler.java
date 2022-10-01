package dev.linkedlogics.service.logic.handler;

import dev.linkedlogics.context.LogicContext;

public class PublishHandler extends LogicHandler {
	public PublishHandler() {

	}
	
	public PublishHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(LogicContext context, Object result) {
		super.handle(context, result);
	}

	@Override
	public void handleError(LogicContext context, Throwable error) {
		super.handleError(context, error);
	}
}
