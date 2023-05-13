package io.linkedlogics.service.common.config;

import io.linkedlogics.service.config.Config;
import io.linkedlogics.service.config.Prefix;
import io.linkedlogics.service.config.ServiceConfig;

@Prefix("services.consumer")
public interface QueueConsumerServiceConfig extends ServiceConfig {
	
	@Config(key = "delay", description = "Sleep time when no messages")
	public Integer getDelay(Integer defaultValue);
}
