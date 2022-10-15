package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.BranchLogicDefinition;
import dev.linkedlogics.model.process.helper.LogicPositioner;

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
					return HandlerResult.nextCandidate(candidatePosition + LogicPositioner.BRANCH_LEFT);
				} else if (branchLogic.getRightLogic() != null) {
					return HandlerResult.nextCandidate(candidatePosition + LogicPositioner.BRANCH_RIGHT);
				} else {
					return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
				}
			}
			
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}