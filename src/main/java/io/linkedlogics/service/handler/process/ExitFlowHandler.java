package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.ExitLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class ExitFlowHandler extends ProcessFlowHandler {
	public ExitFlowHandler() {

	}

	public ExitFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get() instanceof ExitLogicDefinition) {
			log(context, "exiting", candidatePosition, Flow.TERMINATE);
			return HandlerResult.endOfCandidates();
		} else {
			log(context, "no exit", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}