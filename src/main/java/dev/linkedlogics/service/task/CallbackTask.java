package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.OutputHandler;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import lombok.Getter;

@Getter
public class CallbackTask extends LinkedLogicsTask {
	private Object result;
	
	public CallbackTask(LogicContext context, Object result) {
		super(context,  new OutputHandler(new ErrorHandler(new ProcessHandler())));
		this.result = result;
	}

	@Override
	public void run() {
		try {
			handler.handle(context, result);
		} catch (Throwable e) {
			new ErrorHandler(new ProcessHandler()).handleError(context, e);
		}
	}
}
