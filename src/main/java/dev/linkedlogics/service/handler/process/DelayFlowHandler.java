package dev.linkedlogics.service.handler.process;

import java.time.OffsetDateTime;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.service.SchedulerService;
import dev.linkedlogics.service.SchedulerService.Schedule;
import dev.linkedlogics.service.ServiceLocator;

public class DelayFlowHandler extends ProcessFlowHandler {
	public DelayFlowHandler() {

	}
	
	public DelayFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getDelay() != null && candidate.get().getDelay().getSeconds() > 0) {
			
			if (context.getStatus() == Status.SCHEDULED) {
				context.setStatus(Status.STARTED);
				return super.handle(candidate, candidatePosition, context);
			} else {
				context.setStatus(Status.SCHEDULED);
				OffsetDateTime scheduledAt = OffsetDateTime.now().plusSeconds(candidate.get().getDelay().getSeconds());
				Schedule schedule = new Schedule(context.getId(), null, candidatePosition, scheduledAt, SchedulerService.ScheduleType.DELAY); 
				ServiceLocator.getInstance().getSchedulerService().schedule(schedule);
				return HandlerResult.noCandidate();
			}
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
