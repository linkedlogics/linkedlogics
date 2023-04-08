package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.exception.AsyncTimeoutException;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.TrackingHandler;
import dev.linkedlogics.service.handler.logic.ValidHandler;

public class AsyncCallbackExpireTask extends LinkedLogicsTask {
	
	public AsyncCallbackExpireTask(Context context) {
		super(context, new ValidHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler())))));
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
