package dev.linkedlogics.model.process;

import dev.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SavepointLogicDefinition extends BaseLogicDefinition {
	
	public SavepointLogicDefinition() {
		super(ProcessLogicTypes.SAVEPOINT);
	}
	
	public SavepointLogicDefinition clone() {
		SavepointLogicDefinition clone = new SavepointLogicDefinition();
		return clone;
	}

	public static class SavepointLogicBuilder extends LogicBuilder<SavepointLogicBuilder, SavepointLogicDefinition> {
		public SavepointLogicBuilder() {
			super(new SavepointLogicDefinition());
		}
	}
}
