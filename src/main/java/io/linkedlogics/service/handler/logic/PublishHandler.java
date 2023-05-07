package io.linkedlogics.service.handler.logic;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextLog;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.ScriptLogicDefinition;
import io.linkedlogics.model.process.SingleLogicDefinition;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.SchedulerService;
import io.linkedlogics.service.SchedulerService.Schedule;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.TriggerService;
import io.linkedlogics.service.handler.process.HandlerResult;
import io.linkedlogics.service.local.LocalProcessorService;
import io.linkedlogics.service.task.ScriptTask;
import io.linkedlogics.service.task.StartTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PublishHandler extends LogicHandler {

	public PublishHandler() {

	}
	
	public PublishHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(Context context, Object result) {
		HandlerResult handlerResult = (HandlerResult) result;
		if (handlerResult.isEndOfCandidates()) {
			log.debug(log(context, "execution " + (context.getError() == null ? "finished" : "failed")).toString());
			finishContext(context);
		} else if (handlerResult.getSelectedLogic().isPresent()) {
			log.debug(log(context, "execution continues").toString());
			continueContext(context, handlerResult);
		} else {
			log.debug(log(context, "execution paused").toString());
			pauseContext(context);
		}
		
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		super.handleError(context, error);
	}
	
	private void continueContext(Context context, HandlerResult handlerResult) {
		if (handlerResult.getSelectedLogic().get() instanceof SingleLogicDefinition) {
			continueLogic(context, handlerResult);
		} else if (handlerResult.getSelectedLogic().get() instanceof ScriptLogicDefinition) {
			continueScript(context, handlerResult);
		} else {
			throw new RuntimeException("logic must implement SingleLogicDefinition or ScriptLogicDefinition");
		}
	}

	private void finishContext(Context context) {
		ServiceLocator.getInstance().getContextService().set(context);
		ServiceLocator.getInstance().getCallbackService().publish(context);
		
		List<TriggerService.Trigger> triggers = ServiceLocator.getInstance().getTriggerService().get(context.getId());
		if (triggers != null && !triggers.isEmpty()) {
			triggers.stream().forEach(t -> {
				ServiceLocator.getInstance().getProcessorService().process(new StartTask(Context.fromTrigger(context, t)));
			});
		}
	}
	
	private void pauseContext(Context context) {
		timeoutContext(context);
		ServiceLocator.getInstance().getContextService().set(context);
	}
	
	private void timeoutContext(Context context) {	
		if (context.getExpiresAt() == null || context.getExpiresAt().isBefore(OffsetDateTime.now())) {
			int timeout = ((LocalProcessorService) ServiceLocator.getInstance().getProcessorService()).getConfig().getTimeout();
			context.setExpiresAt(OffsetDateTime.now().plusSeconds(timeout));
		}
		
		Schedule schedule = new Schedule(context.getId(), context.getLogicId(), context.getLogicPosition(), context.getExpiresAt(), SchedulerService.ScheduleType.TIMEOUT); 
		ServiceLocator.getInstance().getSchedulerService().schedule(schedule);
		context.setExpiresAt(null);
	}
	
	private void continueScript(Context context, HandlerResult handlerResult) {
		ScriptLogicDefinition logic = (ScriptLogicDefinition) handlerResult.getSelectedLogic().get();
		
		context.setLogicScript(logic.getExpression().getExpression());
		context.setLogicPosition(logic.getPosition());
		context.setLogicReturnAs(logic.getReturnAs());
		context.setLogicReturnAsMap(logic.isReturnAsMap());
		context.setSubmittedAt(OffsetDateTime.now());
		context.setOutput(new HashMap<>());
		context.setStatus(Status.STARTED);
		context.setUpdatedAt(OffsetDateTime.now());
		
		ServiceLocator.getInstance().getContextService().set(context);
		ServiceLocator.getInstance().getProcessorService().process(new ScriptTask(context));
	}

	private void continueLogic(Context context, HandlerResult handlerResult) {
		SingleLogicDefinition logic = (SingleLogicDefinition) handlerResult.getSelectedLogic().get();
		
		context.setLogicId(logic.getLogicId());
		context.setLogicVersion(logic.getLogicVersion());
		context.setLogicPosition(logic.getPosition());
		context.setLogicReturnAs(logic.getReturnAs());
		context.setLogicScript(null);
		context.setSubmittedAt(OffsetDateTime.now());
		context.setApplication(logic.getApplication());
		context.setInput(getInputs(context, logic));
		context.setOutput(null);
		context.setStatus(Status.STARTED);
		context.setUpdatedAt(OffsetDateTime.now());
		
		timeoutContext(context);
		ServiceLocator.getInstance().getContextService().set(context);
		
		boolean localBypass = ((LocalProcessorService) ServiceLocator.getInstance().getProcessorService()).getConfig().getBypass(true);
		
		if (context.getApplication() == null || (localBypass && context.getApplication().equals(LinkedLogics.getApplicationName()))) {
			ServiceLocator.getInstance().getConsumerService().consume(Context.forPublish(context));
		} else {
			ServiceLocator.getInstance().getPublisherService().publish(Context.forPublish(context));
		}
	}

	private Map<String, Object> getInputs(Context context, SingleLogicDefinition logic) {
		final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
		Map<String, Object> inputs = new HashMap<>();
		logic.getInputs().entrySet().stream().forEach(e -> {
			if (e.getValue() instanceof ExpressionLogicDefinition) {
				inputs.put(e.getKey(), evaluator.evaluate(((ExpressionLogicDefinition) e.getValue()).getExpression(), context.getParams()));
			} else {
				inputs.put(e.getKey(), e.getValue());
			}
		});
		return inputs;
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}
