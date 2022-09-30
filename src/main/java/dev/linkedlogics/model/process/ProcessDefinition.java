package dev.linkedlogics.model.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
public class ProcessDefinition {
	public static final int LATEST_VERSION = -1;
	
	private String id;
	private int version = LATEST_VERSION;
	private Map<String, Object> inputs = new HashMap<>();
	
	@Getter(value = AccessLevel.PACKAGE)
	private List<BaseLogicDefinition> logics = new ArrayList<BaseLogicDefinition>();
	@Getter(value = AccessLevel.PUBLIC)
	private Map<String, BaseLogicDefinition> positions;
	
	public List<BaseLogicDefinition> cloneLogics() {
		return logics.stream().map(l -> l.cloneLogic()).collect(Collectors.toList());
	}
	
	public static class ProcessBuilder {
		private ProcessDefinition process;
		
		public ProcessBuilder() {
			this.process = new ProcessDefinition();
		}
		
		public ProcessBuilder id(String id) {
			this.process.setId(id);
			return this;
		}
		
		public ProcessBuilder version(int version) {
			this.process.setVersion(version);
			return this;
		}
		
		public ProcessBuilder add(BaseLogicDefinition logic) {
			if (!this.process.getLogics().isEmpty()) {
				this.process.getLogics().get(this.process.getLogics().size() - 1).setAdjacentLogic(logic);
			}
			this.process.getLogics().add(logic);
			return this;
		}
		
		public ProcessBuilder input(String key, Object value) {
			this.process.getInputs().put(key, value);
			return this;
		}
		
		public ProcessBuilder inputs(Map<String, Object> inputs) {
			this.process.getInputs().putAll(inputs);
			return this;
		}
		
		public ProcessDefinition build() {
			Map<String, BaseLogicDefinition> positions = new HashMap<>();
			for (int i = 0; i < this.process.getLogics().size(); i++) {
				LogicPositioner.setPosition(this.process.getLogics().get(i), positions, String.valueOf(i+1));
			}
			this.process.setPositions(positions);
			
			this.process.getLogics().forEach(l -> {
				this.process.getInputs().entrySet().forEach(e -> {
					l.getInputMap().putIfAbsent(e.getKey(), e.getValue());
				});
			});
			
			return this.process;
		}
	}
	
	public static ProcessBuilder createProcess(String id, int version) {
		return new ProcessBuilder().id(id).version(version);
	}
	
	public static ProcessBuilder createProcess(String id) {
		return new ProcessBuilder().id(id).version(LATEST_VERSION);
	}
}

	