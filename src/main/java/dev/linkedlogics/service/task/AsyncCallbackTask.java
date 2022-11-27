package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.OutputHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import dev.linkedlogics.service.handler.logic.PublishHandler;
import dev.linkedlogics.service.handler.logic.ValidHandler;
import lombok.Getter;

@Getter
public class AsyncCallbackTask extends LinkedLogicsTask {
	private Object result;
	
	public AsyncCallbackTask(Context context, Object result) {
		super(context, new ValidHandler(new OutputHandler(new ErrorHandler(new ProcessHandler(new PublishHandler())))));
		this.result = result;
	}

	@Override
	public void handle() {
		try {
			handler.handle(context, result);
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
