package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class SuccessFlowHandler extends ProcessFlowHandler {
	public SuccessFlowHandler() {

	}

	public SuccessFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidatePosition.equals(context.getLogicPosition())) {
			if (context.getError() == null && candidate.get() instanceof SingleLogicDefinition && ((SingleLogicDefinition) candidate.get()).getCompensationLogic() != null) {
				log(context, "adding compensation logic to compensables", candidatePosition, Flow.CONTINUE);
				context.getCompensables().add(((SingleLogicDefinition)candidate.get()).getCompensationLogic().getPosition());
			} else {
				log(context, "nothing to compensate", candidatePosition, Flow.CONTINUE);
			}
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
