package io.linkedlogics.service.local;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.service.ConfigurableService;
import io.linkedlogics.service.SchedulerService;
import io.linkedlogics.service.local.config.LocalSchedulerServiceConfig;
import lombok.AllArgsConstructor;

public class LocalSchedulerService extends ConfigurableService<LocalSchedulerServiceConfig> implements SchedulerService {
	private ScheduledExecutorService service;
	
	public LocalSchedulerService() {
		super(LocalSchedulerServiceConfig.class);
	}
	
	@Override
	public void start() {
		Optional<Integer> threads = getConfig().getThreads();
		if (threads.isEmpty()) {
			service = Executors.newSingleThreadScheduledExecutor();
		} else {
			service = Executors.newScheduledThreadPool(threads.get());
		}
	}
	
	@Override
	public void stop() {
		if (service != null) {
			service.shutdownNow();
		}
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
			handle(schedule);
		}
	}
}
	
