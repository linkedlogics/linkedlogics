package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;

public class EmptyFlowHandler extends ProcessFlowHandler {
	public EmptyFlowHandler() {

	}

	public EmptyFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		return super.handle(candidate, candidatePosition, context);
	}
}
