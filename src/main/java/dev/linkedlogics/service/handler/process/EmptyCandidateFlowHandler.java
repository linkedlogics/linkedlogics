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
				log(context, "no candidate, starting with first candidate", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			}
			String parentLogicPosition = parentLogicPosition(candidatePosition);
			
			if (parentLogicPosition.equals("")) {
				log(context, "no candidate and no parent", candidatePosition, Flow.TERMINATE);
				return HandlerResult.endOfCandidates();
			} else if (context.getError() != null) {
				log(context, "no candidate, continue with parent", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(parentLogicPosition);
			} else {
				log(context, "no candidate, continue with adjacent", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(adjacentLogicPosition(parentLogicPosition));
			}
		} else {
			log(context, "candidate is not empty", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
