package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.handler.logic.AsyncHandler;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.InvokeHandler;
import dev.linkedlogics.service.handler.logic.OutputHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;

public class ProcessorTask extends LinkedLogicsTask {
	public ProcessorTask(LogicContext context) {
		super(context, new AsyncHandler(new InvokeHandler(new OutputHandler(new ErrorHandler(new ProcessHandler())))));
	}
	
	@Override
	public void run() {
		try {
			ServiceLocator.getInstance().getCallbackService().setContextId(context.getId());
			handler.handle(context, null);
		} catch (Throwable e) {
			new ErrorHandler().handleError(context, e);
		} finally {
			ServiceLocator.getInstance().getCallbackService().unsetContextId();
		}
	}
}
