package dev.linkedlogics.service.process.handler;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.GroupLogicDefinition;

public class GroupFlowHandler extends ProcessFlowHandler {
	public GroupFlowHandler() {

	}

	public GroupFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get() instanceof GroupLogicDefinition) {
			return HandlerResult.nextCandidate(candidatePosition + ".1");
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}