package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.JumpLogicDefinition;

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
				return HandlerResult.nextCandidate(jump.getTargetPosition());
			}

			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}