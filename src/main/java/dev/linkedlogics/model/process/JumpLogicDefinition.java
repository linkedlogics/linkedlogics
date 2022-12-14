package dev.linkedlogics.model.process;

import dev.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
public class JumpLogicDefinition extends BaseLogicDefinition {
	private String targetLabel;
	@Setter
	private String targetPosition;
	
	public JumpLogicDefinition(String targetLabel) {
		super(ProcessLogicTypes.JUMP);
		this.targetLabel = targetLabel;
	}
	
	public JumpLogicDefinition clone() {
		JumpLogicDefinition clone = new JumpLogicDefinition(getTargetLabel());
		return clone;
	}
	
	public static class JumpLogicBuilder extends LogicBuilder<JumpLogicBuilder, JumpLogicDefinition> {
		public JumpLogicBuilder(String label) {
			super(new JumpLogicDefinition(label));
		}
	}
}
