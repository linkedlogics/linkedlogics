package io.linkedlogics.service.handler.process;

import static io.linkedlogics.context.ContextLog.log;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.helper.LogicPositioner;

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
					ContextFlow.branch().position(candidatePosition).name(candidate.get().getId()).result("TRUE").info();
					trace(context, "branch is satisfied going LEFT", candidatePosition, Flow.RESET);
					log(context).handler(this).logic(branchLogic).message("branch is satisfied going LEFT").info();
					return HandlerResult.nextCandidate(candidatePosition + LogicPositioner.BRANCH_LEFT);
				} else if (branchLogic.getRightLogic() != null) {
					ContextFlow.branch().position(candidatePosition).name(candidate.get().getId()).result("FALSE").info();
					trace(context, "branch is not satisfied going RIGHT", candidatePosition, Flow.RESET);
					log(context).handler(this).logic(branchLogic).message("branch is satisfied going RIGHT").info();
					return HandlerResult.nextCandidate(candidatePosition + LogicPositioner.BRANCH_RIGHT);
				} else {
					ContextFlow.branch().position(candidatePosition).name(candidate.get().getId()).result("FALSE").info();
					trace(context, "branch is not satisfied", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			}
			trace(context, "not a branch", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}