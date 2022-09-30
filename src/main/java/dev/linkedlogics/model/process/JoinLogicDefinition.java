package dev.linkedlogics.model.process;

import java.util.Optional;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Getter
public class JoinLogicDefinition {
	private String[] key;
	
	public JoinLogicDefinition(String[] key) {
		this.key = key;
	}
}
