package dev.linkedlogics.service.task;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextLog;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.handler.logic.ErrorHandler;
import dev.linkedlogics.service.handler.logic.LogicHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@AllArgsConstructor
@Slf4j
public abstract class LinkedLogicsTask implements Runnable {
	protected Context context;
	protected LogicHandler handler;
	
	@Override
	public void run() {
		try {
			ServiceLocator.getInstance().getAsyncService().setContextId(context.getId());
			log.debug(log(context, "task is started").toString());
			handle();
		} catch (Throwable e) {
			new ErrorHandler().handleError(context, e);
		} finally {
			ServiceLocator.getInstance().getAsyncService().unsetContextId();
		}
	}
	
	protected abstract void handle() ;
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.task(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}
