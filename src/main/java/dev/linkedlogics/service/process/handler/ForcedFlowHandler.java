package dev.linkedlogics.service.process.handler;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;

public class ForcedFlowHandler extends ProcessFlowHandler {
	public ForcedFlowHandler() {

	}

	public ForcedFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && context.getError() != null) {
			if (candidate.get().isForced()) {
				return super.handle(candidate, candidatePosition, context);
			} else {
				return HandlerResult.nextCandidate(candidatePosition);
			}
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}