package io.linkedlogics.model.process.helper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalProcessServiceConfig;

public abstract class LogicPositioner {
	public static final String BRANCH_LEFT = "L";
	public static final String BRANCH_RIGHT = "R";
	public static final String COMPENSATE = "Z";
	public static final String ERROR = "E";

	public static void setPositions(ProcessDefinition process) {
		Map<String, BaseLogicDefinition> positions = new HashMap<>();
		Map<String, BaseLogicDefinition> labels = new HashMap<>();
		
		for (int i = 0; i < process.getLogics().size(); i++) {
			LogicPositioner.setPosition(process.getLogics().get(i), positions, labels, String.valueOf(i+1));
		}
		process.setPositions(positions);
		process.setLabels(labels);
		
		process.getLogics().forEach(l -> {
			process.getInputs().entrySet().forEach(e -> {
				if (new ServiceConfiguration().getConfig(LocalProcessServiceConfig.class).getParentInputsHasPriority().orElse(Boolean.FALSE)) {
					l.getInputs().put(e.getKey(), e.getValue());
				} else {
					l.getInputs().putIfAbsent(e.getKey(), e.getValue());
				}
			});
		});
	}

	private static void setPosition(BaseLogicDefinition logic, Map<String, BaseLogicDefinition> positions, Map<String, BaseLogicDefinition> labels, String position) {
		logic.setPosition(position);
		positions.put(position, logic);
		Optional.ofNullable(logic.getLabel()).ifPresent(l -> labels.put(l.getLabel(), logic));

		if (logic instanceof GroupLogicDefinition || logic instanceof ProcessLogicDefinition) {
			GroupLogicDefinition group = (GroupLogicDefinition) logic;
			for (int i = 0; i < group.getLogics().size(); i++) {
				setPosition(group.getLogics().get(i), positions, labels, position + "." + (i+1));
			}
		} else if (logic instanceof BranchLogicDefinition) {
			BranchLogicDefinition branch = (BranchLogicDefinition) logic;
			setPosition(branch.getLeftLogic(), positions, labels, position + BRANCH_LEFT);
			if (branch.getRightLogic() != null) {
				setPosition(branch.getRightLogic(), positions, labels, position + BRANCH_RIGHT);
			}
		} else if (logic instanceof SingleLogicDefinition && ((SingleLogicDefinition) logic).getCompensationLogic() != null) {
			setPosition(((SingleLogicDefinition) logic).getCompensationLogic(), positions, labels, position + COMPENSATE);
			((SingleLogicDefinition) logic).getCompensationLogic().setForced(true);
		} 

		if (logic.getError() != null && logic.getError().getErrorLogic() != null) {
			setPosition(logic.getError().getErrorLogic(), positions, labels, position + ERROR);
			logic.getError().getErrorLogic().setForced(true);
		}
	}
}
