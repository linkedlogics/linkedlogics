package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.exception.AsyncTimeoutException;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.logic.handler.ErrorHandler;
import dev.linkedlogics.service.logic.handler.ProcessHandler;

public class CallbackExpireTask extends LinkedLogicsTask {
	
	public CallbackExpireTask(LogicContext context) {
		super(context, new ErrorHandler(new ProcessHandler()));
	}
	
	@Override
	public void run() {
		try {
			ServiceLocator.getInstance().getCallbackService().remove(context.getId()).ifPresent(c -> {
				handler.handleError(c, new AsyncTimeoutException(context.getLogicId(), context.getLogicVersion()));
			});
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
