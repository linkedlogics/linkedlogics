package dev.linkedlogics.service.local;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dev.linkedlogics.config.LinkedLogicsConfiguration;
import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.SchedulerService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.task.DelayedTask;
import dev.linkedlogics.service.task.RetryTask;
import lombok.AllArgsConstructor;

public class LocalSchedulerService implements SchedulerService {
	private ScheduledExecutorService service;
	
	@Override
	public void start() {
		int threads = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.scheduler.threads", -1);
		if (threads == -1) {
			service = Executors.newSingleThreadScheduledExecutor();
		} else {
			service = Executors.newScheduledThreadPool(threads);
		}
	}
	
	@Override
	public void stop() {
		service.shutdownNow();
	}

	@Override
	public void schedule(Schedule schedule) {
		service.schedule(new ScheduledTask(schedule), Duration.between(OffsetDateTime.now(), schedule.getExpiresAt()).toMillis(), TimeUnit.MILLISECONDS);
	}

	@AllArgsConstructor
	private class ScheduledTask implements Runnable {
		private Schedule schedule;

		@Override
		public void run() {
			if (schedule.getType() == ScheduleType.DELAY) {
				ServiceLocator.getInstance().getProcessorService().process(new DelayedTask(LogicContext.fromSchedule(schedule)));
			} else if (schedule.getType() == ScheduleType.RETRY) {
				ServiceLocator.getInstance().getProcessorService().process(new RetryTask(LogicContext.fromSchedule(schedule)));
			} 
		}
	}
}
	
