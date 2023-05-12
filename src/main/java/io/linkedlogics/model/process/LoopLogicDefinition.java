package io.linkedlogics.model.process;

import java.util.Arrays;
import java.util.stream.Collectors;

import io.linkedlogics.context.Context;
import io.linkedlogics.model.ProcessLogicTypes;
import io.linkedlogics.service.ServiceLocator;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoopLogicDefinition extends GroupLogicDefinition {
	private ExpressionLogicDefinition expression;
	
	public LoopLogicDefinition() {
		super(ProcessLogicTypes.LOOP);
	}
	
	public boolean isSatisfied(Context context) {
		return (Boolean) ServiceLocator.getInstance().getEvaluatorService().evaluate(expression.getExpression(), context.getParams());
	}
	
	public LoopLogicDefinition clone() {
		LoopLogicDefinition clone = new LoopLogicDefinition();
		clone.setExpression(getExpression().cloneLogic());
		clone.setLogics(this.getLogics().stream().map(l -> l.clone()).collect(Collectors.toList()));
		return clone;
	}

	public static class LoopLogicBuilder extends LogicBuilder<LoopLogicBuilder, LoopLogicDefinition> {
		public LoopLogicBuilder(ExpressionLogicDefinition expression, BaseLogicDefinition... logics) {
			super(new LoopLogicDefinition());
			getLogic().setExpression(expression);
			this.getLogic().addLogics(Arrays.stream(logics).collect(Collectors.toList()));
		}

		public LoopLogicBuilder retry(int maxRetries, int delay) {
			this.getLogic().setRetry(new RetryLogicDefinition.RetryLogicBuilder(maxRetries, delay).build());
			return this;
		}
		
		public LoopLogicBuilder retry(RetryLogicDefinition retry) {
			this.getLogic().setRetry(retry);
			return this;
		}
		
		public LoopLogicBuilder fork(String fork) {
			this.getLogic().setFork(new ForkLogicDefinition(fork));
			return this;
		}
		
		public LoopLogicBuilder fork() {
			this.getLogic().setFork(new ForkLogicDefinition());
			return this;
		}
		
		@Override
		public LoopLogicDefinition build() {
			this.getLogic().getLogics().forEach(l -> {
				this.getLogic().getInputs().entrySet().forEach(e -> {
					l.getInputs().putIfAbsent(e.getKey(), e.getValue());
				});
			});
			return super.build();
		}
	}
}
