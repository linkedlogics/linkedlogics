package dev.linkedlogics.service.handler.logic;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.service.ServiceLocator;

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
				super.handle(context, result);
			} else {
				super.handleError(context, new RuntimeException("context not valid"));
			}
		} else {
			super.handleError(context, new RuntimeException("context not found"));
		}
	}
}
