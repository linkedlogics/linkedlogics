package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.GroupLogicDefinition;
import dev.linkedlogics.model.process.ProcessLogicDefinition;

public class GroupFlowHandler extends ProcessFlowHandler {
	public GroupFlowHandler() {

	}

	public GroupFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && (candidate.get() instanceof GroupLogicDefinition || candidate.get() instanceof ProcessLogicDefinition)) {
			return HandlerResult.nextCandidate(candidatePosition + ".1");
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}