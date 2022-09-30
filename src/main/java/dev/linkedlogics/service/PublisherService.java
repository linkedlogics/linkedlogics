package dev.linkedlogics.service;

import dev.linkedlogics.context.LogicContext;

public interface PublisherService extends LinkedLogicsService {
	
	void publish(LogicContext context);
}
