package dev.linkedlogics.model.process;

import dev.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;

@Getter
public class LabelLogicDefinition extends TypedLogicDefinition {
	private String label;
	
	public LabelLogicDefinition(String label) {
		super(ProcessLogicTypes.LABEL);
		this.label = label;
	}
	
	public LabelLogicDefinition cloneLogic() {
		LabelLogicDefinition clone = new LabelLogicDefinition(getLabel());
		return clone;
	}
}
