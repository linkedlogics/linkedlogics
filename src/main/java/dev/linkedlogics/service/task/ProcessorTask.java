package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.handler.logic.AsyncHandler;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.InvokeHandler;
import dev.linkedlogics.service.handler.logic.MetricsHandler;
import dev.linkedlogics.service.handler.logic.OutputHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;

public class ProcessorTask extends LinkedLogicsTask {
	public ProcessorTask(LogicContext context) {
		super(context, new InvokeHandler(new MetricsHandler(new AsyncHandler(new OutputHandler(new ErrorHandler(new ProcessHandler()))))));
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
