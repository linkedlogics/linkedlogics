package dev.linkedlogics.model.process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import dev.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupLogicDefinition extends BaseLogicDefinition {
	protected List<BaseLogicDefinition> logics = new LinkedList<BaseLogicDefinition>();
	
	public GroupLogicDefinition() {
		super(ProcessLogicTypes.GROUP);
	}
	
	public GroupLogicDefinition(String type) {
		super(type);
	}
	
	public GroupLogicDefinition clone() {
		GroupLogicDefinition clone = new GroupLogicDefinition();
		clone.setLogics(this.getLogics().stream().map(l -> l.clone()).collect(Collectors.toList()));
		return clone;
	}
	
	public String toString() {
		return "group";
	}
	
	protected void addLogics(List<BaseLogicDefinition> logics) {
		logics.forEach(l -> {
			if (!getLogics().isEmpty()) {
				getLogics().get(getLogics().size() - 1).setAdjacentLogic(l);
			}
			getLogics().add(l);
			l.setParentLogic(this);
		});
	}
	
	public static class GroupLogicBuilder extends LogicBuilder<GroupLogicBuilder, GroupLogicDefinition> {
		public GroupLogicBuilder(BaseLogicDefinition... logics) {
			super(new GroupLogicDefinition());
			this.getLogic().addLogics(Arrays.stream(logics).collect(Collectors.toList()));
		}
		
		public GroupLogicBuilder retry(int maxRetries, int delay) {
			this.getLogic().setRetry(new RetryLogicDefinition.RetryLogicBuilder(maxRetries, delay).build());
			return this;
		}
		
		public GroupLogicBuilder retry(RetryLogicDefinition retry) {
			this.getLogic().setRetry(retry);
			return this;
		}
		
		public GroupLogicBuilder fork(String fork) {
			this.getLogic().setFork(new ForkLogicDefinition(fork));
			return this;
		}
		
		public GroupLogicBuilder fork() {
			this.getLogic().setFork(new ForkLogicDefinition());
			return this;
		}
		
		@Override
		public GroupLogicDefinition build() {
			this.getLogic().getLogics().forEach(l -> {
				this.getLogic().getInputs().entrySet().forEach(e -> {
					l.getInputs().putIfAbsent(e.getKey(), e.getValue());
				});
			});
			return super.build();
		}
	}
}
