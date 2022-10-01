package dev.linkedlogics.model.process;

import lombok.Getter;

@Getter
public class ExpressionLogicDefinition {
	private String expression;
	
	public ExpressionLogicDefinition(String expression) {
		this.expression = expression;
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
