package dev.linkedlogics.service.process.handler;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.SavepointLogicDefinition;

public class SavepointFlowHandler extends ProcessFlowHandler {
	public SavepointFlowHandler() {

	}

	public SavepointFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof SavepointLogicDefinition) {
				context.getCompensables().clear();
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			}

			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}