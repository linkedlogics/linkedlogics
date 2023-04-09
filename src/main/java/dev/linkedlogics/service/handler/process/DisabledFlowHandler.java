package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class DisabledFlowHandler extends ProcessFlowHandler {
	public DisabledFlowHandler() {

	}
	
	public DisabledFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}
	
	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get().getDisabled() != null && candidate.get().getDisabled()) {
				log(context, "logic is disabled", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			} else {
				log(context, "logic is enabled", candidatePosition, Flow.CONTINUE);
				return super.handle(candidate, candidatePosition, context);
			}
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}