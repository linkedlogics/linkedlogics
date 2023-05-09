package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.SavepointLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

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
				trace(context, "savepoint reached, compensations are cleared", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			}
			trace(context, "no savepoint", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}