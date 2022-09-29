package dev.linkedlogics.service;

import dev.linkedlogics.context.LogicContext;

public interface ConsumerService extends LinkedLogicsService {
	
	boolean consume(LogicContext context);
}
