package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.context.ContextError.ErrorType;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.FailLogicDefinition;

public class FailFlowHandler extends ProcessFlowHandler {
	public FailFlowHandler() {

	}

	public FailFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get() instanceof FailLogicDefinition) {
			FailLogicDefinition fail = (FailLogicDefinition) candidate.get();
			ContextError error = new ContextError();
			error.setCode(fail.getErrorCode() != null ? fail.getErrorCode() : -1);
			error.setMessage(fail.getErrorMessage());
			error.setType(ErrorType.PERMANENT);
			context.setError(error);

			return HandlerResult.endOfCandidates();
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}