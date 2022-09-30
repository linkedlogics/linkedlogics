package dev.linkedlogics.service;

import dev.linkedlogics.context.LogicContext;

public interface CallbackService extends LinkedLogicsService {
	
	void set(LogicContext context);
	
	LogicContext remove(String contextId);
	
	void callback(String contextId, Object result);
}
