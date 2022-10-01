package dev.linkedlogics.context;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.service.LogicService;
import dev.linkedlogics.service.TriggerService.Trigger;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogicContext {
	private String id;

	private String logicId;
	private int logicVersion = LogicService.LATEST_VERSION;
	private String position;
	private String application;

	private Map<String, Object> input = new HashMap<>();
	private Map<String, Object> output = new HashMap<>();

	private ContextError error;

	public static LogicContext fromLogic(Context context, SingleLogicDefinition logic) {
		LogicContext logicContext = new LogicContext();
		logicContext.setId(context.getId());
		logicContext.setLogicId(logic.getLogicId());
		logicContext.setLogicVersion(logic.getLogicVersion());
		logicContext.setPosition(logic.getPosition());
		logicContext.setApplication(logic.getApplication());
		logicContext.setInput(logic.getInputMap());
		logicContext.setOutput(null);
		return logicContext;
	}

	public static LogicContext fromTrigger(Context context, Trigger trigger) {
		LogicContext logicContext = new LogicContext();
		logicContext.setId(trigger.getContextId());
		logicContext.setLogicId(null);
		logicContext.setPosition(trigger.getPosition());
		logicContext.setError(context.getError());
		logicContext.setOutput(context.getParams());
		logicContext.setApplication(context.getApplication());
		
		return logicContext;
	}
}
