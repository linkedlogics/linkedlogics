package dev.linkedlogics.service;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface SchedulerService extends LinkedLogicsService {
	
	void schedule(Schedule schedule);

	@Getter
	@AllArgsConstructor
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