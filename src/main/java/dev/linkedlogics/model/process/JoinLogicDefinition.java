package dev.linkedlogics.model.process;

import lombok.Getter;

@Getter
public class JoinLogicDefinition {
	private String[] key;
	
	public JoinLogicDefinition(String[] key) {
		this.key = key;
	}
}
