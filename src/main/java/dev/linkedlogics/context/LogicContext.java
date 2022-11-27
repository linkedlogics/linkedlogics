//package dev.linkedlogics.context;
//
//import java.time.OffsetDateTime;
//import java.util.HashMap;
//import java.util.Map;
//
//import dev.linkedlogics.service.LogicService;
//import dev.linkedlogics.service.SchedulerService.Schedule;
//import dev.linkedlogics.service.TriggerService.Trigger;
//import lombok.Getter;
//import lombok.Setter;
//
//@Setter
//@Getter
//public class LogicContext {
//	private String id;
//
//	private String logicId;
//	private int logicVersion = LogicService.LATEST_VERSION;
//	private String logicReturnAs;
//	private String position;
//	private String application;
//
//	private Map<String, Object> input = new HashMap<>();
//	private Map<String, Object> output = new HashMap<>();
//	
//	private OffsetDateTime createdAt;
//	private OffsetDateTime executedAt;
//	private long executedIn;
//
//	private ContextError error;
//
//	public static LogicContext fromLogic(Context context) {
//		LogicContext logicContext = new LogicContext();
//		logicContext.setId(context.getId());
//		logicContext.setLogicId(context.getLogicId());
//		logicContext.setLogicVersion(context.getLogicVersion());
//		logicContext.setLogicReturnAs(context.getLogicReturnAs());
//		logicContext.setPosition(context.getLogicPosition());
//		logicContext.setApplication(context.getApplication());
//		logicContext.setInput(context.getInput());
//		logicContext.setOutput(new HashMap<>());
//		logicContext.setCreatedAt(context.getSubmittedAt());
//		return logicContext;
//	}
//
//	public static LogicContext fromTrigger(Context context, Trigger trigger) {
//		LogicContext logicContext = new LogicContext();
//		logicContext.setId(trigger.getContextId());
//		logicContext.setLogicId(null);
//		logicContext.setPosition(trigger.getPosition());
//		logicContext.setError(context.getError());
//		logicContext.setOutput(context.getParams());
//		logicContext.setApplication(context.getApplication());
//		
//		return logicContext;
//	}
//	
//	public static LogicContext fromSchedule(Schedule schedule) {
//		LogicContext logicContext = new LogicContext();
//		logicContext.setId(schedule.getContextId());
//		logicContext.setLogicId(schedule.getLogicId());
//		logicContext.setPosition(schedule.getLogicPosition());
//		return logicContext;
//	}
//}
