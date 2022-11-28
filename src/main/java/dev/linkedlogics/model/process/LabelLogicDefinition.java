package dev.linkedlogics.model.process;

import lombok.Getter;

@Getter
public class LabelLogicDefinition {
	private String label;
	
	public LabelLogicDefinition(String label) {
		this.label = label;
	}
	
	public LabelLogicDefinition cloneLogic() {
		LabelLogicDefinition clone = new LabelLogicDefinition(getLabel());
		return clone;
	}
}
