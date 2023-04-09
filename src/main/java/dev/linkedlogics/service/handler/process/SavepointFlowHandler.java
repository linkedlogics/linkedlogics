package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.SavepointLogicDefinition;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

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
				log(context, "savepoint reached, compensations are cleared", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			}
			log(context, "no savepoint", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}