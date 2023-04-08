package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.TrackingHandler;
import dev.linkedlogics.service.handler.logic.ValidHandler;
import lombok.Getter;

@Getter
public class AsyncCallbackErrorTask extends LinkedLogicsTask {
	private Throwable error;
	
	public AsyncCallbackErrorTask(Context context, Throwable error) {
		super(context, new ValidHandler(new ErrorHandler(new ProcessHandler(new TrackingHandler(new PublishHandler())))));
		this.error = error;
	}

	@Override
	public void handle() {
		try {
			handler.handleError(context, error);
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
