package io.linkedlogics.service.handler.logic;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextLog;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ErrorHandler extends LogicHandler {
	
	public ErrorHandler() {

	}
	
	public ErrorHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		log.debug(ContextLog.builder(context).handler(this.getClass().getSimpleName()).message("no error").build().toString());
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		error.printStackTrace();
		context.setError(ContextError.of(error));
		log.error(log(context, error).toString());
		super.handleError(context, error);
	}
	
	private ContextLog log(Context context, Throwable error) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(error.getLocalizedMessage())
				.errorCode(context.getError().getCode())
				.errorMessage(context.getError().getMessage())
				.errorType(context.getError().getType())
				.build();
	}
}
