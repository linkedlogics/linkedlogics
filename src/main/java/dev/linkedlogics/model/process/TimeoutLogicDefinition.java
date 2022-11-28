package dev.linkedlogics.model.process;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TimeoutLogicDefinition {
	private int seconds;
	
	public TimeoutLogicDefinition cloneLogic() {
		return new TimeoutLogicDefinition(getSeconds());
	}
}
