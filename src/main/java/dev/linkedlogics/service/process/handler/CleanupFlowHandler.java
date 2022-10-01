package dev.linkedlogics.service.process.handler;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;

public class CleanupFlowHandler extends ProcessFlowHandler {
	public CleanupFlowHandler() {

	}

	public CleanupFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && context.getLogicPosition().equals(candidatePosition)) {
			return HandlerResult.noCandidate();
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
