package dev.linkedlogics.model.process;

import dev.linkedlogics.model.ProcessLogicTypes;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class FailLogicDefinition extends BaseLogicDefinition {
	private Integer errorCode;
	private String errorMessage;
	
	public FailLogicDefinition() {
		super(ProcessLogicTypes.FAIL);
	}
	
	public FailLogicDefinition clone() {
		FailLogicDefinition clone = new FailLogicDefinition();
		return clone;
	}
	
	public static class FailLogicBuilder extends LogicBuilder<FailLogicBuilder, FailLogicDefinition> {
		public FailLogicBuilder() {
			super(new FailLogicDefinition());
		}
		
		public FailLogicBuilder code(int errorCode) {
			this.getLogic().setErrorCode(errorCode);
			return this;
		}
		
		public FailLogicBuilder message(String errorMessage) {
			this.getLogic().setErrorMessage(errorMessage);
			return this;
		}
	}
}
