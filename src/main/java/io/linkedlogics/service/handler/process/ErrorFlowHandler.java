package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.ErrorLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

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
					trace(context, "error handled with logic", candidatePosition, Flow.RESET);
					ContextFlow.error(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("error handled with logic " + candidate.get().getError().getErrorLogic().getPosition()).log(context);
					return HandlerResult.nextCandidate(candidate.get().getError().getErrorLogic().getPosition());
				} else {
					trace(context, "error handled", candidatePosition, Flow.RESET);
					ContextFlow.error(candidatePosition).name(candidate.get().getName()).result(Boolean.TRUE).message("error handled").log(context);
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			} else {
				trace(context, "error occured", candidatePosition, Flow.CONTINUE);
				return super.handle(candidate, candidatePosition, context);
			}
		} else {
			trace(context, "no error", candidatePosition, Flow.CONTINUE);
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