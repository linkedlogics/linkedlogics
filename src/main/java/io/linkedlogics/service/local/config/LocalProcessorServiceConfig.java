package io.linkedlogics.service.local.config;

import java.util.Optional;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.processor")
public interface LocalProcessorServiceConfig extends ServiceConfig {

	@Config(key = "threads", description = "Number of threads to execute tasks")
	public Optional<Integer> getThreads();
	
	@Config(key = "timeout", description = "Timeout for executing logic")
	public Integer getTimeout();
	
	@Config(key = "bypass", description = "Skips messaging when next logic is also on same application")
	public Boolean getBypass(Boolean defaultValue);
}
