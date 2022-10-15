package dev.linkedlogics.model.process;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DelayedLogicDefinition {
	private int delay;
	
	public DelayedLogicDefinition cloneLogic() {
		return new DelayedLogicDefinition(getDelay());
	}
}
