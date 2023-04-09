package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class ForcedFlowHandler extends ProcessFlowHandler {
	public ForcedFlowHandler() {

	}

	public ForcedFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && context.getError() != null) {
			if (candidate.get().getForced() != null && candidate.get().getForced()) {
				log(context, "logic is forced", candidatePosition, Flow.CONTINUE);
				return super.handle(candidate, candidatePosition, context);
			} else {
				log(context, "logic is not forced", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(candidatePosition);
			}
		} else {
			log(context, "no error no forcing", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}