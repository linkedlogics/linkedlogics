package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;

public class EmptyCandidateFlowHandler extends ProcessFlowHandler {
	public EmptyCandidateFlowHandler() {

	}
	
	public EmptyCandidateFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isEmpty()) {
			if (candidatePosition.equals("0")) {
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			}
			String parentLogicPosition = parentLogicPosition(candidatePosition);
			
			if (parentLogicPosition.equals("")) {
				return HandlerResult.endOfCandidates();
			} else if (context.getError() != null) {
				return HandlerResult.nextCandidate(parentLogicPosition);
			} else {
				return HandlerResult.nextCandidate(adjacentLogicPosition(parentLogicPosition));
			}
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
