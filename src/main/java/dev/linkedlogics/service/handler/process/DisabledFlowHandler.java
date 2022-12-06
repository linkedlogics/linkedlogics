package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;

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
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			} else {
				return super.handle(candidate, candidatePosition, context);
			}
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}