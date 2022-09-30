package dev.linkedlogics.model.process;

import java.util.HashSet;
import java.util.Set;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorLogicDefinition {
	private BaseLogicDefinition errorLogic;
	private Set<Integer> errorCodeSet = new HashSet<>();
	private Set<String> errorMessageSet = new HashSet<>();
	private boolean rethrow;
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error(Integer... errorCodes) {
		return new ErrorLogicDefinitionBuilder().errorCodeSet(Set.of(errorCodes));
	}
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error(String... errorMessages) {
		return new ErrorLogicDefinitionBuilder().errorMessageSet(Set.of(errorMessages));
	}
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error() {
		return new ErrorLogicDefinitionBuilder();
	}
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error(BaseLogicDefinition errorLogic) {
		return new ErrorLogicDefinitionBuilder().errorLogic(errorLogic);
	}
}
