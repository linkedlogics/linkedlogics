package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;

public class OutputFlowHandler extends ProcessFlowHandler {
	public OutputFlowHandler() {

	}

	public OutputFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()
				&& context.getOutput() != null
					&& context.getError() == null 
						&& candidatePosition.equals(context.getLogicPosition())) {
			context.getOutput().entrySet().forEach(e -> {
				context.getParams().put(e.getKey(), e.getValue());
			});
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
