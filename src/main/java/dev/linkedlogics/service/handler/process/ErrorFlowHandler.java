package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.ErrorLogicDefinition;

public class ErrorFlowHandler extends ProcessFlowHandler {
	public ErrorFlowHandler() {

	}

	public ErrorFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && context.getError() != null) {

			if (candidate.get().getError() != null && matches(context.getError(), candidate.get().getError())) {
				context.setError(null);
				
				if (candidate.get().getError().getErrorLogic() != null) {
					return HandlerResult.nextCandidate(candidate.get().getError().getErrorLogic().getPosition());
				} else {
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			} else {
				return super.handle(candidate, candidatePosition, context);
			}
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}

	private boolean matches(ContextError error, ErrorLogicDefinition errorDefinition) {
		if (errorDefinition.getErrorCodeSet() == null && errorDefinition.getErrorMessageSet() == null) {
			return true;
		}
		
		if (errorDefinition.getErrorCodeSet() != null && errorDefinition.getErrorCodeSet().contains(error.getCode())) {
			return true;
		} else if (error.getMessage() != null && errorDefinition.getErrorMessageSet() != null) { 
			return errorDefinition.getErrorMessageSet().stream().filter(s -> error.getMessage().contains(s)).findAny().isPresent();
		}

		return false;
	}
}