package dev.linkedlogics.service.handler.logic;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.exception.LogicTimeoutException;
import dev.linkedlogics.service.ServiceLocator;

public class TimeoutHandler extends LogicHandler {
	public TimeoutHandler() {

	}
	
	public TimeoutHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context logicContext, Object result) {
		Optional<Context> context = ServiceLocator.getInstance().getContextService().get(logicContext.getId());
		
		if (context.isPresent()) {
			if (context.get().getLogicPosition().startsWith(logicContext.getLogicPosition()) && context.get().getStatus() != Status.FINISHED && context.get().getStatus() != Status.FAILED) {
				super.handleError(logicContext, new LogicTimeoutException(logicContext.getLogicId()));
			} else {
				super.handle(logicContext, result);
			}
		} else {
			super.handle(logicContext, result);
		}
	}
}
