package dev.linkedlogics.service;

import java.time.OffsetDateTime;

public interface MetricsService extends LinkedLogicsService {
	void set(String key, long value, OffsetDateTime timestamp);
}
