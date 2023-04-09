package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextLog;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.BranchLogicDefinition;
import dev.linkedlogics.model.process.helper.LogicPositioner;
import dev.linkedlogics.service.handler.logic.ProcessHandler;

public class BranchFlowHandler extends ProcessFlowHandler {
	public BranchFlowHandler() {

	}

	public BranchFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof BranchLogicDefinition) {
				BranchLogicDefinition branchLogic = (BranchLogicDefinition) candidate.get(); 
				if (branchLogic.isSatisfied(context)) {
					log(context, "branch is satisfied going LEFT", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(candidatePosition + LogicPositioner.BRANCH_LEFT);
				} else if (branchLogic.getRightLogic() != null) {
					log(context, "branch is not satisfied going RIGHT", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(candidatePosition + LogicPositioner.BRANCH_RIGHT);
				} else {
					log(context, "branch is not satisfied", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			}
			log(context, "not a branch", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}