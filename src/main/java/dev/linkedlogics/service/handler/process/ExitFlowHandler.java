package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.ExitLogicDefinition;

public class ExitFlowHandler extends ProcessFlowHandler {
	public ExitFlowHandler() {

	}

	public ExitFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get() instanceof ExitLogicDefinition) {
			return HandlerResult.endOfCandidates();
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}