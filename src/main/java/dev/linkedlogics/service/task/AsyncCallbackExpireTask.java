package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.exception.AsyncTimeoutException;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;

public class AsyncCallbackExpireTask extends LinkedLogicsTask {
	
	public AsyncCallbackExpireTask(LogicContext context) {
		super(context, new ErrorHandler(new ProcessHandler()));
	}
	
	@Override
	public void handle() {
		try {
			ServiceLocator.getInstance().getAsyncService().remove(context.getId()).ifPresent(c -> {
				handler.handleError(c, new AsyncTimeoutException(context.getLogicId(), context.getLogicVersion()));
			});
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
