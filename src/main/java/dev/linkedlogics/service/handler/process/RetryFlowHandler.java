package dev.linkedlogics.service.handler.process;

import java.time.OffsetDateTime;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.context.ContextError.ErrorType;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.RetryLogicDefinition;
import dev.linkedlogics.service.SchedulerService;
import dev.linkedlogics.service.SchedulerService.Schedule;
import dev.linkedlogics.service.ServiceLocator;

public class RetryFlowHandler extends ProcessFlowHandler {
	public RetryFlowHandler() {

	}
	
	public RetryFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getRetry() != null) {
			RetryLogicDefinition retry = candidate.get().getRetry();
			
			if (context.getError() != null && matches(context.getError(), retry)) {
				if (context.getError().getType() == ErrorType.TEMPORARY) {
					int retries = context.getRetries().getOrDefault(candidatePosition, 1);
					if (retries < retry.getMaxRetries()) {
						context.getRetries().put(candidatePosition, retries + 1);
						context.setError(null);
						
						if (candidate.get().getRetry().getDelay() > 0) {
							context.setStatus(Status.SCHEDULED);
							OffsetDateTime scheduledAt = OffsetDateTime.now().plusSeconds(candidate.get().getRetry().getDelay());
							Schedule schedule = new Schedule(context.getId(), null, candidatePosition, scheduledAt, SchedulerService.ScheduleType.RETRY); 
							ServiceLocator.getInstance().getSchedulerService().schedule(schedule);
							return HandlerResult.noCandidate();
						} else {
							return HandlerResult.selectCandidate(candidate);
						}
					} else {
						context.getRetries().remove(candidatePosition);
					}
				}
			} else {
				context.getRetries().remove(candidatePosition);
			}
		}

		return super.handle(candidate, candidatePosition, context);
	}
	
	private boolean matches(ContextError error, RetryLogicDefinition retryDefinition) {
		boolean matchResult = false;
		
		if ((retryDefinition.getErrorCodeSet() == null || retryDefinition.getErrorCodeSet().isEmpty()) 
				&& (retryDefinition.getErrorMessageSet() == null || retryDefinition.getErrorMessageSet().isEmpty())) {
			matchResult = true;
		} else if (retryDefinition.getErrorCodeSet() != null && retryDefinition.getErrorCodeSet().contains(error.getCode())) {
			matchResult = true;
		} else if (error.getMessage() != null 
				&& retryDefinition.getErrorMessageSet() != null
					&& retryDefinition.getErrorMessageSet().stream().filter(s -> error.getMessage().contains(s)).findAny().isPresent()) { 
			matchResult = true;
		}

		return retryDefinition.isExclude() ? !matchResult : matchResult;
	}
}
