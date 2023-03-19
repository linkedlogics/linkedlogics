package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.handler.logic.AsyncHandler;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.InvokeHandler;
import dev.linkedlogics.service.handler.logic.LoggerHandler;
import dev.linkedlogics.service.handler.logic.MetricsHandler;
import dev.linkedlogics.service.handler.logic.OutputHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.ValidHandler;

public class ProcessorTask extends LinkedLogicsTask {
	public ProcessorTask(Context context) {
		super(context, new ValidHandler(new InvokeHandler(new LoggerHandler(new MetricsHandler(new AsyncHandler(new OutputHandler(new ErrorHandler(new ProcessHandler(new PublishHandler())))))))));
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
