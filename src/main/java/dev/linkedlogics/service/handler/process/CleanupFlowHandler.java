package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.helper.LogicPositioner;

public class CleanupFlowHandler extends ProcessFlowHandler {
	public CleanupFlowHandler() {

	}

	public CleanupFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidatePosition.equals(context.getLogicPosition()) && !candidatePosition.endsWith(LogicPositioner.COMPENSATE)) {
			return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
