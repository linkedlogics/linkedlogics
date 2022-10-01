package dev.linkedlogics.service.process.handler;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;

public class SuccessFlowHandler extends ProcessFlowHandler {
	public SuccessFlowHandler() {

	}

	public SuccessFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && context.getLogicPosition().equals(candidatePosition)) {
			if (context.getError() == null && candidate.get() instanceof SingleLogicDefinition && ((SingleLogicDefinition) candidate.get()).getCompensationLogic() != null) {
				context.getCompensables().add(((SingleLogicDefinition)candidate.get()).getCompensationLogic().getPosition());
			}
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
