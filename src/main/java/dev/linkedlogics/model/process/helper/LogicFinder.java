package dev.linkedlogics.model.process.helper;

import java.util.ArrayList;
import java.util.List;

import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.BranchLogicDefinition;
import dev.linkedlogics.model.process.ErrorLogicDefinition;
import dev.linkedlogics.model.process.GroupLogicDefinition;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;

public class LogicFinder {
	public static List<BaseLogicDefinition> findLogics(ProcessDefinition process, Class<? extends BaseLogicDefinition> logicDefinitionClass) {
		List<BaseLogicDefinition> foundList = new ArrayList<>();
		traverse(process.getLogics(), foundList, logicDefinitionClass);
		return foundList;
	}
	
	private static void traverse(List<BaseLogicDefinition> logicList, List<BaseLogicDefinition> foundList, Class<? extends BaseLogicDefinition> logicDefinitionClass) {
		logicList.forEach(l -> {
			if (logicDefinitionClass.isAssignableFrom(l.getClass())) {
				foundList.add(l);
			}
			
			if (l instanceof BranchLogicDefinition) {
				BranchLogicDefinition branch = (BranchLogicDefinition) l;
				traverse(List.of(branch.getLeftLogic()), foundList, logicDefinitionClass);
				if (branch.getRightLogic() != null) {
					traverse(List.of(branch.getRightLogic()), foundList, logicDefinitionClass);
				}
			} else if (l instanceof GroupLogicDefinition) {
				GroupLogicDefinition group = (GroupLogicDefinition) l;
				traverse(group.getLogics(), foundList, logicDefinitionClass);
			} else if (l instanceof SingleLogicDefinition) {
				SingleLogicDefinition single = (SingleLogicDefinition) l;
				if (single.getCompensationLogic() != null) {
					traverse(List.of(single.getCompensationLogic()), foundList, logicDefinitionClass);
				}
			} else if (l instanceof ErrorLogicDefinition) {
				ErrorLogicDefinition error = (ErrorLogicDefinition) l;
				if (error.getErrorLogic() != null) {
					traverse(List.of(error.getErrorLogic()), foundList, logicDefinitionClass);
				}
			}
			
			if (l.getError() != null && l.getError().getErrorLogic() != null) {
				traverse(List.of(l.getError().getErrorLogic()), foundList, logicDefinitionClass);
			}
		});
	}
}
