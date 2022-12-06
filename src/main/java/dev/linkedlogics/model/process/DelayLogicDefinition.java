package dev.linkedlogics.model.process;

import dev.linkedlogics.model.ProcessLogicTypes;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DelayLogicDefinition extends TypedLogicDefinition {
	private int seconds;
	
	public DelayLogicDefinition(int seconds) {
		super(ProcessLogicTypes.DELAY);
		this.seconds = seconds;
	}
	
	public DelayLogicDefinition cloneLogic() {
		return new DelayLogicDefinition(getSeconds());
	}
}
