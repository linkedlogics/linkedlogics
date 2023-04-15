package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.VerifyLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

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
					log(context, "not verified", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(candidatePosition);
				} else {
					log(context, "verified", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			} else {
				log(context, "no verify", candidatePosition, Flow.CONTINUE);
			}

			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}