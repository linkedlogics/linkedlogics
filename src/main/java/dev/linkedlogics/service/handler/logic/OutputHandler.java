package dev.linkedlogics.service.handler.logic;

import java.util.HashMap;
import java.util.Map;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.model.LogicDefinition;

public class OutputHandler extends LogicHandler {
	
	public OutputHandler() {

	}
	
	public OutputHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(LogicContext context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		
		if (logic.getMethod().getReturnType() != Void.class && result != null) {
			if (context.getOutput() == null) {
				context.setOutput(new HashMap<>());
			}
			
			String returnAs = context.getLogicReturnAs() != null ? context.getLogicReturnAs() : logic.getReturnAs();
			
			if (result instanceof Map && logic.isReturnMap()) {
				context.getOutput().putAll((Map) result);
			} else if (logic.getReturnAs() != null) {
				context.getOutput().put(returnAs, result);
			}	
		}
		
		super.handle(context, null);
	}
}
