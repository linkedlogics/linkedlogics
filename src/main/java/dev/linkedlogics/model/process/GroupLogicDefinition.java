package dev.linkedlogics.model.process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GroupLogicDefinition extends BaseLogicDefinition {
	protected List<BaseLogicDefinition> logics = new LinkedList<BaseLogicDefinition>();
	
	public GroupLogicDefinition clone() {
		GroupLogicDefinition clone = new GroupLogicDefinition();
		clone.setLogics(this.getLogics().stream().map(l -> l.clone()).collect(Collectors.toList()));
		clone.getInputMap().putAll(getInputMap());
		return clone;
	}
	
	public String toString() {
		return "group";
	}
	
	public static class GroupLogicBuilder extends LogicBuilder<GroupLogicBuilder, GroupLogicDefinition> {
		public GroupLogicBuilder(BaseLogicDefinition... logics) {
			super(new GroupLogicDefinition());
			Arrays.stream(logics).forEach(this::addLogic);
		}
		
		private void addLogic(BaseLogicDefinition logic) {
			if (!this.getLogic().getLogics().isEmpty()) {
				this.getLogic().getLogics().get(this.getLogic().getLogics().size() - 1).setAdjacentLogic(logic);
			}
			this.getLogic().getLogics().add(logic);
			logic.setParentLogic(this.getLogic());
		}
		
		public GroupLogicBuilder retry(int maxRetries, int delay) {
			this.getLogic().setRetry(RetryLogicDefinition.builder().maxRetries(maxRetries).delay(delay).build());
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
				this.getLogic().getInputMap().entrySet().forEach(e -> {
					l.getInputMap().putIfAbsent(e.getKey(), e.getValue());
				});
			});
			return super.build();
		}
	}
}
