package dev.linkedlogics.service.process.handler;

import java.util.Optional;

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
					&& context.getError() != null 
						&& context.getLogicId() != null 
							&& context.getLogicPosition().equals(candidatePosition)) {
			context.getOutput().entrySet().forEach(e -> {
				context.getParams().put(e.getKey(), e.getValue());
			});
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
