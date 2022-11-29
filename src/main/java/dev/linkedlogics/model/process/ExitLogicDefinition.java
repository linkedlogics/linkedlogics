package dev.linkedlogics.model.process;

import lombok.Getter;

@Getter
public class ExitLogicDefinition extends BaseLogicDefinition {
	
	public ExitLogicDefinition clone() {
		ExitLogicDefinition clone = new ExitLogicDefinition();
		return clone;
	}
	
	public static class ExitLogicBuilder extends LogicBuilder<ExitLogicBuilder, ExitLogicDefinition> {
		public ExitLogicBuilder() {
			super(new ExitLogicDefinition());
		}
	}
}
