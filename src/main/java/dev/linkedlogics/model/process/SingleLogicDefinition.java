package dev.linkedlogics.model.process;

import dev.linkedlogics.model.process.GroupLogicDefinition.GroupLogicBuilder;
import dev.linkedlogics.service.LogicService;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
public class SingleLogicDefinition extends BaseLogicDefinition {
	private String logicId;
	private int logicVersion = LogicService.LATEST_VERSION;
	private String application;
	private BaseLogicDefinition compensationLogic;
	
	public SingleLogicDefinition clone() {
		SingleLogicDefinition clone = new SingleLogicDefinition();
		clone.setLogicId(getLogicId());
		clone.setLogicVersion(getLogicVersion());
		clone.getInputMap().putAll(getInputMap());
		return clone;
	}
	
	public static class SingleLogicBuilder extends LogicBuilder<SingleLogicBuilder, SingleLogicDefinition> {
		public SingleLogicBuilder() {
			super(new SingleLogicDefinition());
		}
		
		public SingleLogicBuilder id(String id) {
			this.getLogic().setLogicId(id);
			return this;
		}
		
		public SingleLogicBuilder version(int version) {
			this.getLogic().setLogicVersion(version);
			return this;
		}
		
		public SingleLogicBuilder compensate(SingleLogicDefinition compensationLogic) {
			this.getLogic().setCompensationLogic(compensationLogic);
			return this;
		}
		
		public SingleLogicBuilder fork(String fork) {
			this.getLogic().setFork(new ForkLogicDefinition(fork));
			return this;
		}
		
		public SingleLogicBuilder fork() {
			this.getLogic().setFork(new ForkLogicDefinition());
			return this;
		}
		
		public SingleLogicBuilder retry(int maxRetries, int delay) {
			this.getLogic().setRetry(RetryLogicDefinition.builder().maxRetries(maxRetries).delay(delay).build());
			return this;
		}
	}
	
	public String toString() {
		return logicId;
	}
	
	public static SingleLogicBuilder logic(String id, int version) {
		return new SingleLogicBuilder().id(id).version(version);
	}
	
	public static SingleLogicBuilder logic(String id) {
		return new SingleLogicBuilder().id(id).version(LogicService.LATEST_VERSION);
	}
}