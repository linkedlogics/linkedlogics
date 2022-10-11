package dev.linkedlogics.model.process;

import java.util.Optional;

import lombok.Getter;

@Getter
public class ForkLogicDefinition {
	private Optional<String> key;
	
	public ForkLogicDefinition(String key) {
		this.key = Optional.of(key);
	}
	
	public ForkLogicDefinition() {
		this.key = Optional.empty();
	}
}
