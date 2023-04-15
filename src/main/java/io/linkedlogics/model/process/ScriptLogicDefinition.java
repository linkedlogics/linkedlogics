package io.linkedlogics.model.process;

import java.util.Optional;

import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.service.ServiceLocator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ScriptLogicDefinition extends BaseLogicDefinition {
	private ExpressionLogicDefinition expression;
	private String returnAs;
	private boolean returnAsMap;
	
	public ScriptLogicDefinition() {
		super(ProcessLogicTypes.SCRIPT);
	}
	
	public ScriptLogicDefinition clone() {
		return null;
	}

	public static class ScriptLogicBuilder extends LogicBuilder<ScriptLogicBuilder, ScriptLogicDefinition> {
		public ScriptLogicBuilder(ExpressionLogicDefinition expression) {
			super(new ScriptLogicDefinition());
			getLogic().setExpression(expression);
		}
		
		public ScriptLogicBuilder returnAs(String returnAs) {
			this.getLogic().setReturnAs(returnAs);
			return this;
		}
		
		public ScriptLogicBuilder returnAsMap() {
			this.getLogic().setReturnAsMap(true);
			return this;
		}

		@Override
		public ScriptLogicDefinition build() {
			ServiceLocator.getInstance().getEvaluatorService().checkSyntax(this.getLogic().getExpression().getExpression()).ifPresent(err -> {
				throw new RuntimeException("syntax error " + (this.getLogic().getId() != null ? "at " + this.getLogic().getId() : "") + "\n" + err);
			});
			
			return super.build();
		}
	}
}
