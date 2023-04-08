package dev.linkedlogics.service.handler.logic;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextLog;
import dev.linkedlogics.model.LogicDefinition;
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
