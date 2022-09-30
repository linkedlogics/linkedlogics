package dev.linkedlogics.model.process;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import dev.linkedlogics.service.ServiceLocator;
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
	
	public static GroupLogicBuilder group(BaseLogicDefinition... logics) {
		return new GroupLogicBuilder(logics);
	}
	
	public static GroupLogicBuilder process(String processId, int version) {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId, version).map(w -> {
			List<BaseLogicDefinition> logics = w.cloneLogics();
			BaseLogicDefinition[] logicArray = new BaseLogicDefinition[logics.size()];
			logics.toArray(logicArray);
			
			return new GroupLogicBuilder(logicArray);
		}).orElseThrow(() -> new IllegalArgumentException("missing process " + processId));
	}
	
	public static GroupLogicBuilder process(ProcessDefinition process) {
		List<BaseLogicDefinition> logics = process.cloneLogics();
		BaseLogicDefinition[] logicArray = new BaseLogicDefinition[logics.size()];
		logics.toArray(logicArray);
		
		return new GroupLogicBuilder(logicArray).inputs(process.getInputs());
	}
}
