package dev.linkedlogics.model.process;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DelayLogicDefinition {
	private int seconds;
	
	public DelayLogicDefinition cloneLogic() {
		return new DelayLogicDefinition(getSeconds());
	}
}
