package dev.linkedlogics.model.process;

import lombok.Getter;
import lombok.Setter;

@Getter
public class JoinLogicDefinition {
	private String[] key;
	
	public JoinLogicDefinition(String[] key) {
		this.key = key;
	}
	
	public JoinLogicDefinition cloneLogic() {
		JoinLogicDefinition clone = new JoinLogicDefinition(getKey());
		return clone;
	}
}
