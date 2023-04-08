package dev.linkedlogics.service.handler.logic;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextLog;
import dev.linkedlogics.service.handler.process.HandlerResult;
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
		HandlerResult handlerResult = (HandlerResult) result;
		log.debug(log(context, "tracking context").toString());
		
		if (handlerResult.isEndOfCandidates()) {

		} else if (handlerResult.getSelectedLogic().isPresent()) {

		} else {

		}
		
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
