package dev.linkedlogics.model.process;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.ServiceLocator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class VerifyLogicDefinition extends BaseLogicDefinition {
	private ExpressionLogicDefinition expression;
	private int errorCode = -1;
	private String errorMessage;
	
	public boolean isVerified(Context context) {
		return (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression, context.getParams());
	}
	
	public VerifyLogicDefinition clone() {
		VerifyLogicDefinition clone = new VerifyLogicDefinition();
		clone.setExpression(getExpression().cloneLogic());
		clone.setErrorCode(getErrorCode());
		clone.setErrorMessage(getErrorMessage());
		return clone;
	}

	public static class VerifyLogicBuilder extends LogicBuilder<VerifyLogicBuilder, VerifyLogicDefinition> {
		public VerifyLogicBuilder(ExpressionLogicDefinition expression) {
			super(new VerifyLogicDefinition());
			getLogic().setExpression(expression);
		}

		public VerifyLogicBuilder code(int errorCode) {
			this.getLogic().setErrorCode(errorCode);
			return this;
		}
		
		public VerifyLogicBuilder message(String errorMessage) {
			this.getLogic().setErrorMessage(errorMessage);
			return this;
		}
	}
}
