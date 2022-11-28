package dev.linkedlogics.model.process.helper;

import java.util.HashMap;
import java.util.Map;

import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.BranchLogicDefinition;
import dev.linkedlogics.model.process.ErrorLogicDefinition;
import dev.linkedlogics.model.process.GroupLogicDefinition;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.model.process.ProcessLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;

public abstract class LogicPositioner {
	public static final String BRANCH_LEFT = "L";
	public static final String BRANCH_RIGHT = "R";
	public static final String COMPENSATE = "Z";
	public static final String ERROR = "E";

	public static void setPositions(ProcessDefinition process) {
		Map<String, BaseLogicDefinition> positions = new HashMap<>();
		for (int i = 0; i < process.getLogics().size(); i++) {
			LogicPositioner.setPosition(process.getLogics().get(i), positions, String.valueOf(i+1));
		}
		process.setPositions(positions);

		process.getLogics().forEach(l -> {
			process.getInputs().entrySet().forEach(e -> {
				l.getInputs().putIfAbsent(e.getKey(), e.getValue());
			});
		});
	}

	private static void setPosition(BaseLogicDefinition logic, Map<String, BaseLogicDefinition> positionMap, String position) {
		logic.setPosition(position);
		positionMap.put(position, logic);

		if (logic instanceof GroupLogicDefinition || logic instanceof ProcessLogicDefinition) {
			GroupLogicDefinition group = (GroupLogicDefinition) logic;
			for (int i = 0; i < group.getLogics().size(); i++) {
				setPosition(group.getLogics().get(i), positionMap, position + "." + (i+1));
			}
		} else if (logic instanceof BranchLogicDefinition) {
			BranchLogicDefinition branch = (BranchLogicDefinition) logic;
			setPosition(branch.getLeftLogic(), positionMap, position + BRANCH_LEFT);
			if (branch.getRightLogic() != null) {
				setPosition(branch.getRightLogic(), positionMap, position + BRANCH_RIGHT);
			}
		} else if (logic instanceof SingleLogicDefinition && ((SingleLogicDefinition) logic).getCompensationLogic() != null) {
			setPosition(((SingleLogicDefinition) logic).getCompensationLogic(), positionMap, position + COMPENSATE);
			((SingleLogicDefinition) logic).getCompensationLogic().setForced(true);
		} else if (logic instanceof ErrorLogicDefinition && ((ErrorLogicDefinition) logic).getErrorLogic() != null) {
			setPosition(((ErrorLogicDefinition) logic).getErrorLogic(), positionMap, position + ERROR);
			logic.setForced(true);
		}

		if (logic.getError() != null && logic.getError().getErrorLogic() != null) {
			setPosition(logic.getError().getErrorLogic(), positionMap, position + ERROR);
		}
	}
}
