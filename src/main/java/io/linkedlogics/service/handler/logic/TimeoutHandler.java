package io.linkedlogics.service.handler.logic;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.context.Status;
import io.linkedlogics.exception.LogicTimeoutException;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
				log.error(log(context.get(), "context is timed out").toString());
				super.handleError(logicContext, new LogicTimeoutException(logicContext.getLogicId()));
			}
		}
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}
