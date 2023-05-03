package io.linkedlogics.service.handler.logic;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.context.Status;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CancelHandler extends LogicHandler {
	
	public CancelHandler() {

	}
	
	public CancelHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		if (context.isNotFinished()) {
			context.setStatus(Status.CANCELLED);
			ServiceLocator.getInstance().getCallbackService().publish(context);
			ServiceLocator.getInstance().getContextService().remove(context.getId());
			log.debug(log(context, "context is cancelled").toString());
		} else {
			log.debug(log(context, "context is already finished").toString());
		}
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}
