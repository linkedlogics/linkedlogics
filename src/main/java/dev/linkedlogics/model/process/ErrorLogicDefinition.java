package dev.linkedlogics.model.process;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorLogicDefinition {
	private BaseLogicDefinition errorLogic;
	private Set<Integer> errorCodeSet = new HashSet<>();
	private Set<String> errorMessageSet = new HashSet<>();
	private boolean rethrow;
	
	public static class ErrorLogicDefinitionBuilder {
		protected ErrorLogicDefinition error;
		
		public ErrorLogicDefinitionBuilder() {
			error = new ErrorLogicDefinition();
		}
		
		public ErrorLogicDefinitionBuilder errorCodeSet(Set<Integer> errorCodeSet) {
			error.setErrorCodeSet(errorCodeSet);
			return this;
		}
		
		public ErrorLogicDefinitionBuilder errorMessageSet(Set<String> errorMessageSet) {
			error.setErrorMessageSet(errorMessageSet);
			return this;
		}
		
		public ErrorLogicDefinitionBuilder errorLogic(BaseLogicDefinition errorLogic) {
			error.setErrorLogic(errorLogic);
			return this;
		}
		
		public ErrorLogicDefinition build() {
			return error;
		}
	}
}
