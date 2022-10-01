package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;

public class StartTask extends LinkedLogicsTask {
	public StartTask(LogicContext context) {
		super(context, new ProcessHandler());
	}
	
	@Override
	public void run() {
		try {
			handler.handle(context, null);
		} catch (Throwable e) {
			new ErrorHandler().handleError(context, e);
		}
	}
}
