package dev.linkedlogics.service;

import dev.linkedlogics.context.LogicContext;

public interface CallbackService extends LinkedLogicsService {
	
	boolean set(LogicContext context);
	
	LogicContext remove(String contextId);
	
	boolean callback(String contextId, Object result);
}
