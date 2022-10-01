package dev.linkedlogics.model.process;

import java.util.Map;

public abstract class LogicPositioner {
	public static final String BRANCH_LEFT = "L";
	public static final String BRANCH_RIGHT = "R";
	public static final String COMPENSATE = "Z";
	public static final String ERROR = "E";
	
	public static void setPosition(BaseLogicDefinition logic, Map<String, BaseLogicDefinition> positionMap, String position) {
		logic.setPosition(position);
		positionMap.put(position, logic);
		
		if (logic instanceof GroupLogicDefinition) {
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
		}
		
		if (logic.getError() != null && logic.getError().getErrorLogic() != null) {
			setPosition(logic.getError().getErrorLogic(), positionMap, position + ERROR);
		}
	}
}
