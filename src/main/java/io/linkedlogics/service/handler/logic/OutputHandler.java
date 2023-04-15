package io.linkedlogics.service.handler.logic;

import java.util.Map;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.model.LogicDefinition;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OutputHandler extends LogicHandler {
	
	public OutputHandler() {

	}
	
	public OutputHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		
		if (logic.getMethod().getReturnType() != Void.class && result != null) {
			String returnAs = context.getLogicReturnAs() != null ? context.getLogicReturnAs() : logic.getReturnAs();
			
			if (result instanceof Map && logic.isReturnMap()) {
				context.getOutput().putAll((Map) result);
			} else if (returnAs != null) {
				context.getOutput().put(returnAs, result);
			}	
		}
		
		if (context.getOutput().isEmpty()) {
			log.debug(log(context, "no output").toString());
		} else {
			log.debug(log(context, "setting output").toString());
		}
		
		super.handle(context, null);
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.outputs(context.getOutput())
				.message(message)
				.build();
	}
}
