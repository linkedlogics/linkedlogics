package io.linkedlogics.service.handler.logic;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackingHandler extends LogicHandler {

	public TrackingHandler() {

	}
	
	public TrackingHandler(LogicHandler handler) {
		super(handler);
	}
	
	@Override
	public void handle(Context context, Object result) {
		log.debug(log(context, "tracking context").toString());
		ServiceLocator.getInstance().getTrackingService().track(context);

		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		super.handleError(context, error);
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}