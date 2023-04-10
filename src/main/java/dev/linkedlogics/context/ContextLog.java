package dev.linkedlogics.context;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;

import dev.linkedlogics.context.ContextError.ErrorType;
import dev.linkedlogics.service.ServiceLocator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ContextLog {
	@Builder.Default
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private OffsetDateTime timestamp = OffsetDateTime.now();
	private String step;
	private String candidate;
	private String handler;
	private String contextId;
	private String contextKey;
	private String parentId;
	private String processId;
	private Integer processVersion;
	private String application;
	private String logicId;
	private Integer logicVersion;
	private String logicPosition;

	private String message;
	private String task;
	
	private String logicReturnAs;
	private String logicScript;
	private Map<String, Object> inputs = new HashMap<>();
	private Map<String, Object> outputs = new HashMap<>();
	private Integer errorCode;
	private String errorMessage;
	private ErrorType errorType;
	private String errorStackTrace;

	public static ContextLogBuilder builder(Context context) {
		return new ContextLogBuilder()
			.application(context.getApplication())
			.contextId(context.getId())
			.contextKey(context.getKey())
			.parentId(context.getParentId())
			.processId(context.getProcessId())
			.processVersion(context.getProcessVersion())
			.logicId(context.getLogicId())
			.logicVersion(context.getLogicVersion())
			.logicPosition(context.getLogicPosition());
	}
	
	public String toString() {
		try {
			return ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(this);
		} catch (JsonProcessingException e) {
			return "unable to convert to json" + e.getLocalizedMessage();
		}
	}
}