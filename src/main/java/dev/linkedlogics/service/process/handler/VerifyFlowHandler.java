package dev.linkedlogics.service.process.handler;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.context.ContextError.ErrorType;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.VerifyLogicDefinition;

public class VerifyFlowHandler extends ProcessFlowHandler {
	public VerifyFlowHandler() {

	}

	public VerifyFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof VerifyLogicDefinition) {
				VerifyLogicDefinition verifyLogic = (VerifyLogicDefinition) candidate.get(); 
				if (!verifyLogic.isVerified(context)) {
					context.setError(new ContextError(verifyLogic.getErrorCode(), verifyLogic.getErrorMessage(), ErrorType.PERMANENT));
					return HandlerResult.nextCandidate(candidatePosition);
				} else {
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			}

			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}