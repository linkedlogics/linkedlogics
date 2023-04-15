package io.linkedlogics.service.handler.logic;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.context.Status;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidHandler  extends LogicHandler {

	public ValidHandler() {

	}
	
	public ValidHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		Optional<Context> maybeContext = ServiceLocator.getInstance().getContextService().get(context.getId());
		if (maybeContext.isPresent()) {
			Context fullContext = maybeContext.get();
			if (fullContext.getLogicPosition().equals(context.getLogicPosition()) && fullContext.getStatus() != Status.FINISHED && fullContext.getStatus() != Status.FAILED) {
				log.debug(log(fullContext, "context is valid").toString());
				super.handle(context, result);
			} else {
				log.error(log(fullContext, "context is not valid").toString());
				super.handleError(context, new RuntimeException("context not valid"));
			}
		} else {
			log.error(log(context, "context is not found").toString());
			super.handleError(context, new RuntimeException("context not found"));
		}
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}
