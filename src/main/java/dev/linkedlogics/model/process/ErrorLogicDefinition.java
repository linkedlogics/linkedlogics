package dev.linkedlogics.model.process;

import java.util.HashSet;
import java.util.Set;

import dev.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogicDefinition extends BaseLogicDefinition {
	private BaseLogicDefinition errorLogic;
	private Set<Integer> errorCodeSet = new HashSet<>();
	private Set<String> errorMessageSet = new HashSet<>();
	private boolean throwAgain;
	private Integer throwErrorCode;
	private String throwErrorMessage;
	
	public ErrorLogicDefinition() {
		super(ProcessLogicTypes.ERROR);
	}
	
	public ErrorLogicDefinition clone() {
		ErrorLogicDefinition clone = new ErrorLogicDefinition();
		clone.setErrorCodeSet(new HashSet<>(getErrorCodeSet()));
		clone.setErrorMessageSet(new HashSet<>(getErrorMessageSet()));
		clone.setErrorLogic(errorLogic != null ? errorLogic.cloneLogic() : null);
		clone.setThrowAgain(isThrowAgain());
		clone.setThrowErrorCode(getThrowErrorCode());
		clone.setThrowErrorMessage(getThrowErrorMessage());
		return clone;
	}
	
	public static class ErrorLogicBuilder extends LogicBuilder<ErrorLogicBuilder, ErrorLogicDefinition> {
		public ErrorLogicBuilder() {
			super(new ErrorLogicDefinition());
		}
		
		public ErrorLogicBuilder errorCodeSet(Integer... errorCodes) {
			this.getLogic().setErrorCodeSet(Set.of(errorCodes));
			return this;
		}
		
		public ErrorLogicBuilder errorMessageSet(String... errorMessages) {
			this.getLogic().setErrorMessageSet(Set.of(errorMessages));
			return this;
		}
		
		public ErrorLogicBuilder errorLogic(BaseLogicDefinition errorLogic) {
			this.getLogic().setErrorLogic(errorLogic);
			return this;
		}
		
		public ErrorLogicBuilder throwAgain() {
			this.getLogic().setThrowAgain(true);
			return this;
		}
		
		public ErrorLogicBuilder throwAgain(int errorCode, String errorMessage) {
			this.getLogic().setThrowAgain(true);
			this.getLogic().setThrowErrorCode(errorCode);
			this.getLogic().setThrowErrorMessage(errorMessage);
			return this;
		}
	}
}
