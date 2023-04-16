package io.linkedlogics.model.process;

import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.service.ServiceLocator;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class ExpressionLogicDefinition extends TypedLogicDefinition {
	private String expression;
	@Setter(value = AccessLevel.PACKAGE)
	private String source;
	
	public ExpressionLogicDefinition(String expression) {
		super(ProcessLogicTypes.EXPR);
		this.expression = expression;
	}
	
	public ExpressionLogicDefinition cloneLogic() {
		ExpressionLogicDefinition clone = new ExpressionLogicDefinition(expression);
		return clone;
	}
	
	@Getter
	public static class ExpressionLogicDefinitionBuilder {
		protected ExpressionLogicDefinition expression;
		
		public ExpressionLogicDefinitionBuilder(String expr) {
			expression = new ExpressionLogicDefinition(expr);
		}
		
		public ExpressionLogicDefinitionBuilder source(String source) {
			this.expression.setSource(source);
			return this;
		}
		
		public ExpressionLogicDefinition build() {
			ServiceLocator.getInstance().getEvaluatorService().checkSyntax(this.getExpression().getExpression()).ifPresent(err -> {
				throw new RuntimeException("syntax error " + (this.getExpression().getSource() != null ? "at " + this.getExpression().getSource() : "") + "\n" + err);
			});
			return expression;
		}
	}
}
