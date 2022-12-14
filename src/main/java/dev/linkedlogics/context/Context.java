package dev.linkedlogics.context;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import dev.linkedlogics.service.LogicService;
import dev.linkedlogics.service.SchedulerService.Schedule;
import dev.linkedlogics.service.TriggerService.Trigger;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class Context {
	private String id;
	private String key;
	private String parentId;
	private Status status = Status.INITIAL;
	private int version;
	
	private String processId;
	private int processVersion;
	private Map<String, Object> params = new HashMap<>();
	private String startPosition = "0";
	private String endPosition;
	
	private String logicId;
	private int logicVersion = LogicService.LATEST_VERSION;
	private String logicPosition;
	private String logicReturnAs;
	private String application;
	private Map<String, Object> input = new HashMap<>();
	private Map<String, Object> output = new HashMap<>();
	private ContextError error;
	private OffsetDateTime submittedAt;
	private OffsetDateTime executedAt;
	private long executedIn;
	
	private OffsetDateTime createdAt;
	private OffsetDateTime updatedAt;
	private OffsetDateTime finishedAt;
	private OffsetDateTime expiresAt;
	
	private Map<String, Integer> retries = new HashMap<>();
	private List<String> compensables = new ArrayList<>();
	private Map<String, String> joinMap = new HashMap<>();
	
	public Context(String processId, int processVersion, Map<String, Object> params) {
		this.id = UUID.randomUUID().toString();
		this.processId = processId;
		this.processVersion = processVersion;
		this.params.putAll(params);
		this.createdAt = OffsetDateTime.now();
	}
	
	public static Context forPublish(Context context) {
		Context logicContext = new Context();
		logicContext.setId(context.getId());
		logicContext.setLogicId(context.getLogicId());
		logicContext.setLogicVersion(context.getLogicVersion());
		logicContext.setLogicReturnAs(context.getLogicReturnAs());
		logicContext.setLogicPosition(context.getLogicPosition());
		logicContext.setApplication(context.getApplication());
		logicContext.setInput(context.getInput());
		logicContext.setOutput(new HashMap<>());
		logicContext.setCreatedAt(context.getSubmittedAt());
		return logicContext;
	}

	public static Context fromTrigger(Context context, Trigger trigger) {
		Context triggerContext = new Context();
		triggerContext.setId(trigger.getContextId());
		triggerContext.setLogicId(null);
		triggerContext.setLogicPosition(trigger.getPosition());
		triggerContext.setError(context.getError());
		triggerContext.setOutput(context.getParams());
		triggerContext.setApplication(context.getApplication());
		
		return triggerContext;
	}
	
	public static Context fromSchedule(Schedule schedule) {
		Context scheduleContext = new Context();
		scheduleContext.setId(schedule.getContextId());
		scheduleContext.setLogicId(schedule.getLogicId());
		scheduleContext.setLogicPosition(schedule.getLogicPosition());
		return scheduleContext;
	}
}
