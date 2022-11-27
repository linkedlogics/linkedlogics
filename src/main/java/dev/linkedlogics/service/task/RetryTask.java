package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.ValidHandler;

public class RetryTask extends LinkedLogicsTask {
	public RetryTask(Context context) {
		super(context, new ValidHandler(new ProcessHandler(new PublishHandler())));
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
