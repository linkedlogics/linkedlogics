package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.handler.logic.AsyncHandler;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.TrackingHandler;
import dev.linkedlogics.service.handler.logic.MetricsHandler;
import dev.linkedlogics.service.handler.logic.OutputHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.ScriptHandler;
import dev.linkedlogics.service.handler.logic.ValidHandler;

public class ScriptTask extends LinkedLogicsTask {
	public ScriptTask(Context context) {
		super(context, new ValidHandler(new ScriptHandler(new MetricsHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler())))))));
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
