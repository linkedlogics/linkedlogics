package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.ErrorLogicDefinition;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

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
				setError(context, candidate.get().getError());
				
				if (candidate.get().getError().getErrorLogic() != null) {
					log(context, "error handled with logic", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(candidate.get().getError().getErrorLogic().getPosition());
				} else {
					log(context, "error handled", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			} else {
				log(context, "error occured", candidatePosition, Flow.CONTINUE);
				return super.handle(candidate, candidatePosition, context);
			}
		} else {
			log(context, "no error", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
	
	private void setError(Context context, ErrorLogicDefinition errorDefinition) {
		if (!errorDefinition.isThrowAgain()) {
			context.setError(null);
		} else {
			if (errorDefinition.getThrowErrorCode() != null) {
				context.getError().setCode(errorDefinition.getThrowErrorCode());
			}
			
			if (errorDefinition.getThrowErrorMessage() != null) {
				context.getError().setMessage(errorDefinition.getThrowErrorMessage());
			}
		}
	}

	private boolean matches(ContextError error, ErrorLogicDefinition errorDefinition) {
		if ((errorDefinition.getErrorCodeSet() == null || errorDefinition.getErrorCodeSet().isEmpty()) 
				&& (errorDefinition.getErrorMessageSet() == null || errorDefinition.getErrorMessageSet().isEmpty())) {
			return true;
		}
		
		if (errorDefinition.getErrorCodeSet() != null && errorDefinition.getErrorCodeSet().contains(error.getCode())) {
			return true;
		} 
		
		if (error.getMessage() != null 
				&& errorDefinition.getErrorMessageSet() != null
					&& errorDefinition.getErrorMessageSet().stream().filter(s -> error.getMessage().contains(s)).findAny().isPresent()) { 
			return true;
		}

		return false;
	}
}