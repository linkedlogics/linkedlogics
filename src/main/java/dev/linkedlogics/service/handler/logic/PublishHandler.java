package dev.linkedlogics.service.handler.logic;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.config.LinkedLogicsConfiguration;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.service.EvaluatorService;
import dev.linkedlogics.service.SchedulerService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.TriggerService;
import dev.linkedlogics.service.SchedulerService.Schedule;
import dev.linkedlogics.service.handler.process.HandlerResult;
import dev.linkedlogics.service.task.StartTask;
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
			finishContext(context);
		} else if (handlerResult.getSelectedLogic().isPresent()) {
			continueContext(context, handlerResult);
		} else {
			pauseContext(context);
		}
		
		super.handle(context, result);
	}

	@Override
	public void handleError(Context context, Throwable error) {
		super.handleError(context, error);
	}
	
	private void continueContext(Context context, HandlerResult handlerResult) {
		SingleLogicDefinition logic = (SingleLogicDefinition) handlerResult.getSelectedLogic().get();
		
		context.setLogicId(logic.getLogicId());
		context.setLogicVersion(logic.getLogicVersion());
		context.setLogicPosition(logic.getPosition());
		context.setLogicReturnAs(logic.getReturnAs());
		context.setSubmittedAt(OffsetDateTime.now());
		context.setApplication(logic.getApplication());
		context.setInput(getInputs(context, logic));
		context.setOutput(null);
		context.setStatus(Status.STARTED);
		context.setUpdatedAt(OffsetDateTime.now());
		
		timeoutContext(context);
		ServiceLocator.getInstance().getContextService().set(context);
		if (context.getApplication() != null && !context.getApplication().equals(LinkedLogics.getApplicationName())) {
			ServiceLocator.getInstance().getPublisherService().publish(Context.forPublish(context));
		} else {
			ServiceLocator.getInstance().getConsumerService().consume(Context.forPublish(context));
		}
	}

	private void finishContext(Context context) {
		log.info("context finished " + context.getId());
		context.setStatus(context.getError() == null ? Status.FINISHED : Status.FAILED);
		context.setFinishedAt(OffsetDateTime.now());
		ServiceLocator.getInstance().getContextService().set(context);
		ServiceLocator.getInstance().getCallbackService().publish(context);
		
		List<TriggerService.Trigger> triggers = ServiceLocator.getInstance().getTriggerService().get(context.getId());
		log.info("finished context " + context.getId() + " > " + triggers.size());
		if (triggers != null && !triggers.isEmpty()) {
			triggers.stream().forEach(t -> {
				ServiceLocator.getInstance().getProcessorService().process(new StartTask(Context.fromTrigger(context, t)));
			});
		}
		
		ServiceLocator.getInstance().getLoggerService().logContext(context);
	}
	
	private void pauseContext(Context context) {
		timeoutContext(context);
		ServiceLocator.getInstance().getContextService().set(context);
	}
	
	private void timeoutContext(Context context) {	
		if (context.getExpiresAt() == null || context.getExpiresAt().isBefore(OffsetDateTime.now())) {
			int timeout = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.process.timeout", 60);
			context.setExpiresAt(OffsetDateTime.now().plusSeconds(timeout));
		}
		
		Schedule schedule = new Schedule(context.getId(), context.getLogicId(), context.getLogicPosition(), context.getExpiresAt(), SchedulerService.ScheduleType.TIMEOUT); 
		ServiceLocator.getInstance().getSchedulerService().schedule(schedule);
		context.setExpiresAt(null);
	}

	private Map<String, Object> getInputs(Context context, SingleLogicDefinition logic) {
		final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
		Map<String, Object> inputs = new HashMap<>();
		logic.getInputs().entrySet().stream().forEach(e -> {
			if (e.getValue() instanceof ExpressionLogicDefinition) {
				inputs.put(e.getKey(), evaluator.evaluate((ExpressionLogicDefinition) e.getValue(), context.getParams()));
			} else {
				inputs.put(e.getKey(), e.getValue());
			}
		});
		return inputs;
	}
}
