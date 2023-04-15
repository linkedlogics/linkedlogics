package io.linkedlogics.service.handler.logic;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.model.LogicDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncHandler extends LogicHandler {
	
	public AsyncHandler() {

	}
	
	public AsyncHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		
		if (!logic.isReturnAsync()) {
			log.debug(log(context, "async logic is not used").toString());
			super.handle(context, result);	
		} else {
			log.debug(log(context, "async logic is waiting for callback").toString());
		}
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}
