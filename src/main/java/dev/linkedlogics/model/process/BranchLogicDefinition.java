package dev.linkedlogics.model.process;

import java.util.Map.Entry;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.ServiceLocator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BranchLogicDefinition extends BaseLogicDefinition {
	private ExpressionLogicDefinition expression;
	private BaseLogicDefinition leftLogic;
	private BaseLogicDefinition rightLogic;
	
	
	public boolean isSatisfied(Context context) {
		return (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression, context.getParams());
	}
	
	public BranchLogicDefinition clone() {
		BranchLogicDefinition clone = new BranchLogicDefinition();
		clone.setExpression(getExpression().clone());
		clone.setLeftLogic(getLeftLogic().clone());
		clone.setRightLogic(getRightLogic().clone());
		clone.getInputMap().putAll(getInputMap());
		return clone;
	}

	public static class BranchLogicBuilder extends LogicBuilder<BranchLogicBuilder, BranchLogicDefinition> {
		public BranchLogicBuilder(ExpressionLogicDefinition expression, BaseLogicDefinition leftBranch, BaseLogicDefinition rightBranch) {
			super(new BranchLogicDefinition());
			getLogic().setExpression(expression);
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
}
