package dev.linkedlogics.service.handler.logic;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.model.LogicDefinition;

public class AsyncHandler extends LogicHandler {
	
	public AsyncHandler() {

	}
	
	public AsyncHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(LogicContext context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		
		if (!logic.isReturnAsync()) {
			super.handle(context, result);	
		}
	}
}