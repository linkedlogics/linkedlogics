package dev.linkedlogics.service;

import java.time.OffsetDateTime;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.task.DelayedTask;
import dev.linkedlogics.service.task.RetryTask;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public interface SchedulerService extends LinkedLogicsService {
	
	void schedule(Schedule schedule);
	
	default void handle(Schedule schedule) {
		if (schedule.getType() == ScheduleType.DELAY) {
			ServiceLocator.getInstance().getProcessorService().process(new DelayedTask(LogicContext.fromSchedule(schedule)));
		} else if (schedule.getType() == ScheduleType.RETRY) {
			ServiceLocator.getInstance().getProcessorService().process(new RetryTask(LogicContext.fromSchedule(schedule)));
		} 
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class Schedule {
		private String contextId;
		private String logicId;
		private String logicPosition;
		private OffsetDateTime expiresAt;
		private ScheduleType type;
	}
	
	public enum ScheduleType {
		TIMEOUT,
		RETRY,
		DELAY;
	}
}