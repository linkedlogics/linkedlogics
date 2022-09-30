package dev.linkedlogics.service.logic.handler;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.model.LogicDefinition;
import dev.linkedlogics.service.ServiceLocator;

public class AsyncHandler extends LogicHandler {
	
	public AsyncHandler() {

	}
	
	public AsyncHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(LogicContext context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		
		if (logic.isReturnAsync()) {
			ServiceLocator.getInstance().getCallbackService().set(context);
		}
		
		super.handle(context, result);
	}
}
