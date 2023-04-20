package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.FailLogicDefinition;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

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
			log(context, "failing " + error.toString(), candidatePosition, Flow.TERMINATE);
			return HandlerResult.endOfCandidates();
		} else {
			log(context, "no failure", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		}
	}
}