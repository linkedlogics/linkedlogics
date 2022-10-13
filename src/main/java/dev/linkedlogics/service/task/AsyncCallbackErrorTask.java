package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import lombok.Getter;

@Getter
public class AsyncCallbackErrorTask extends LinkedLogicsTask {
	private Throwable error;
	
	public AsyncCallbackErrorTask(LogicContext context, Throwable error) {
		super(context, new ErrorHandler(new ProcessHandler()));
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
