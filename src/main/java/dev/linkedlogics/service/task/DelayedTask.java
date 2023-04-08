package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.TrackingHandler;
import dev.linkedlogics.service.handler.logic.ValidHandler;

public class DelayedTask extends LinkedLogicsTask {
	public DelayedTask(Context context) {
		super(context, new ValidHandler(new ProcessHandler(new TrackingHandler(new PublishHandler()))));
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
