package dev.linkedlogics.model.process;

import java.util.HashSet;
import java.util.Set;

import dev.linkedlogics.model.process.BaseLogicDefinition.LogicBuilder;
import dev.linkedlogics.model.process.RetryLogicDefinition.RetryLogicBuilder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RetryLogicDefinition {
	private int maxRetries;
	private int delay;
	private Set<Integer> errorCodeSet = new HashSet<>();
	private Set<String> errorMessageSet = new HashSet<>();
	private boolean exclude;
	
	public RetryLogicDefinition cloneLogic() {
		RetryLogicDefinition clone = new RetryLogicDefinition();
		clone.setMaxRetries(getMaxRetries());
		clone.setDelay(getDelay());
		clone.setErrorCodeSet(getErrorCodeSet());
		clone.setErrorMessageSet(getErrorMessageSet());
		clone.setExclude(isExclude());
		return clone;
	}
	
	@Getter
	public static class RetryLogicBuilder {
		private RetryLogicDefinition logic;
		
		public RetryLogicBuilder(int maxRetries, int delay) {
			logic = new RetryLogicDefinition();
			logic.setMaxRetries(maxRetries);
			logic.setDelay(delay);
		}
		
		public RetryLogicBuilder errorCodeSet(Integer... errorCodes) {
			this.getLogic().setErrorCodeSet(Set.of(errorCodes));
			return this;
		}
		
		public RetryLogicBuilder errorMessageSet(String... errorMessages) {
			this.getLogic().setErrorMessageSet(Set.of(errorMessages));
			return this;
		}
		
		public RetryLogicBuilder include() {
			this.getLogic().setExclude(false);
			return this;
		}
		
		public RetryLogicBuilder exclude() {
			this.getLogic().setExclude(true);
			return this;
		}
		
		public RetryLogicDefinition build() {
			return logic;
		}
	}
}
