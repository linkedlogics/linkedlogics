package dev.linkedlogics.model.process;

import java.util.Map.Entry;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BranchLogicDefinition extends BaseLogicDefinition {
	private boolean condition;
	private BaseLogicDefinition leftLogic;
	private BaseLogicDefinition rightLogic;
	
	
	public boolean isSatisfied(Context context) {
		return condition;
	}
	
	public BranchLogicDefinition clone() {
		BranchLogicDefinition clone = new BranchLogicDefinition();
		clone.setCondition(isCondition());
		clone.setLeftLogic(getLeftLogic().clone());
		clone.setRightLogic(getRightLogic().clone());
		clone.getInputMap().putAll(getInputMap());
		return clone;
	}

	public static class LogicBranchBuilder extends LogicBuilder<LogicBranchBuilder, BranchLogicDefinition> {
		public LogicBranchBuilder(boolean condition, BaseLogicDefinition leftBranch, BaseLogicDefinition rightBranch) {
			super(new BranchLogicDefinition());
			getLogic().setCondition(condition);
			getLogic().setLeftLogic(leftBranch);
			leftBranch.setParentLogic(this.getLogic());
			if (rightBranch != null) {
				getLogic().setRightLogic(rightBranch);
				rightBranch.setParentLogic(this.getLogic());
			}
		}

		@Override
		public BranchLogicDefinition build() {
			for (Entry<String, Object> entry : this.getLogic().getInputMap().entrySet()) {
				this.getLogic().getLeftLogic().getInputMap().putIfAbsent(entry.getKey(), entry.getValue());
				if (this.getLogic().getRightLogic() != null) {
					this.getLogic().getRightLogic().getInputMap().putIfAbsent(entry.getKey(), entry.getValue());
				}
			}
			return super.build();
		}
	}
	
	public static LogicBranchBuilder branch(boolean condition, BaseLogicDefinition leftBranch, BaseLogicDefinition rightBranch) {
		return new LogicBranchBuilder(condition, leftBranch, rightBranch);
	}
	
	public static LogicBranchBuilder branch(boolean condition, BaseLogicDefinition leftBranch) {
		return new LogicBranchBuilder(condition, leftBranch, null);
	}
}
