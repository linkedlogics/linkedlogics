package dev.linkedlogics.service;

import dev.linkedlogics.context.LogicContext;

public interface ConsumerService extends LinkedLogicsService {
	
	void consume(LogicContext context);
}
