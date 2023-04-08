package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.MetricsHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.TimeoutHandler;
import dev.linkedlogics.service.handler.logic.TrackingHandler;

public class TimeoutTask extends LinkedLogicsTask {
	public TimeoutTask(Context context) {
		super(context, new TimeoutHandler(new MetricsHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler()))))));
	}
	
	@Override
	public void handle() {
		try {
			handler.handle(context, null);
		} catch (Throwable e) {
			new ErrorHandler().handleError(context, e);
		}
	}
}
