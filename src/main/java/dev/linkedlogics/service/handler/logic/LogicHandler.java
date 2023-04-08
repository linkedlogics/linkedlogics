package dev.linkedlogics.service.handler.logic;

import java.util.Optional;

import com.fasterxml.jackson.core.JsonProcessingException;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextLog;
import dev.linkedlogics.model.LogicDefinition;
import dev.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
