package dev.linkedlogics.service.handler.logic;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.service.ServiceLocator;

public class MetricsHandler extends LogicHandler {
	public static final String DELIMITER = ".";
	
	public MetricsHandler() {

	}
	
	public MetricsHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		String key = getKey(context, null);
		long value = getValue(context);
		ServiceLocator.getInstance().getMetricsService().set(key, value, context.getExecutedAt());
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		String key = getKey(context, error);
		long value = getValue(context);
		ServiceLocator.getInstance().getMetricsService().set(key, value, context.getExecutedAt());
		super.handleError(context, error);
	}
	
	private String getKey(Context context, Throwable exception) {
		StringBuilder key = new StringBuilder();
		key.append(LinkedLogics.getApplicationName());
		key.append(DELIMITER);
		key.append(context.getLogicId()).append("[").append(context.getLogicVersion()).append("]");
		
		if (exception != null) {
			ContextError error = ContextError.of(exception);
			key.append(DELIMITER).append("error[").append(error.getCode()).append("]");
		}
		
		return key.toString();
	}
	
	private long getValue(Context context) {
		return context.getExecutedIn();
	}
}
