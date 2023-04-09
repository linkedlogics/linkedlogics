package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.JumpLogicDefinition;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class JumpFlowHandler extends ProcessFlowHandler {
	public JumpFlowHandler() {

	}

	public JumpFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof JumpLogicDefinition) {
				JumpLogicDefinition jump = (JumpLogicDefinition) candidate.get();
				log(context, "jumping", candidatePosition, Flow.RESET);
				return HandlerResult.nextCandidate(jump.getTargetPosition());
			}
			log(context, "no jump", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}