package dev.linkedlogics.context;

import dev.linkedlogics.exception.LogicException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ContextError {
	public static final int DEFAULT_ERROR_CODE = -1;
	
	public enum ErrorType { PERMANENT, TEMPORARY };
	
	private int code;
	private String message;
	private ErrorType type;
	
	public static ContextError of(Throwable e) {
		if (e instanceof LogicException) {
			return new ContextError((((LogicException) e).getErrorCode()), (((LogicException) e).getErrorMessage()), (((LogicException) e).getErrorType()));
		} else {
			return new ContextError(DEFAULT_ERROR_CODE, e.getLocalizedMessage(), ErrorType.TEMPORARY);
		}
	}
}
