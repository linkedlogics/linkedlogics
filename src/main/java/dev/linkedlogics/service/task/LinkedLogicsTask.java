package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.LogicHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class LinkedLogicsTask implements Runnable {
	protected LogicContext context;
	protected LogicHandler handler;
	
	@Override
	public void run() {
		try {
			ServiceLocator.getInstance().getAsyncService().setContextId(context.getId());
			handle();
		} catch (Throwable e) {
			new ErrorHandler().handleError(context, e);
		} finally {
			ServiceLocator.getInstance().getAsyncService().unsetContextId();
		}
	}
	
	protected abstract void handle() ;
}
