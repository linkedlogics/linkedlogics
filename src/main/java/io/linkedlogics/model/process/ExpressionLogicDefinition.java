package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;

@Getter
public class ExpressionLogicDefinition extends TypedLogicDefinition {
	private String expression;
	
	public ExpressionLogicDefinition(String expression) {
		super(ProcessLogicTypes.EXPR);
		this.expression = expression;
	}
	
	public ExpressionLogicDefinition cloneLogic() {
		ExpressionLogicDefinition clone = new ExpressionLogicDefinition(expression);
		return clone;
	}
	
	public static class ExpressionLogicDefinitionBuilder {
		protected ExpressionLogicDefinition expression;
		
		public ExpressionLogicDefinitionBuilder(String expr) {
			expression = new ExpressionLogicDefinition(expr);
		}
		
		public ExpressionLogicDefinition build() {
			return expression;
		}
	}
}
