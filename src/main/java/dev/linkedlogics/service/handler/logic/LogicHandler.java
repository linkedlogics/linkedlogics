package dev.linkedlogics.service.handler.logic;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.LogicDefinition;
import dev.linkedlogics.service.ServiceLocator;

public abstract class LogicHandler {
	protected Optional<LogicHandler> nextHandler;
	
	public LogicHandler() {
		this.nextHandler = Optional.empty();
	}
	
	public LogicHandler(LogicHandler nextHandler) {
		this.nextHandler = Optional.of(nextHandler);
	}
	
	public void handle(Context context, Object result) {
		nextHandler.ifPresent(h -> h.handle(context, result));
	}
	
	public void handleError(Context context, Throwable error) {
		nextHandler.ifPresent(h -> h.handleError(context, error));
	}
	
	protected LogicDefinition findLogic(String logicId, int version) {
		return ServiceLocator.getInstance().getLogicService().getLogic(logicId, version).orElseThrow(() -> new IllegalArgumentException(String.format("logic %s[%d] is not found" , logicId, version)));
	}
}
