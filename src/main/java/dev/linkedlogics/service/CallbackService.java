package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.context.LogicContext;

public interface CallbackService extends LinkedLogicsService {
	
	void set(LogicContext context);
	
	Optional<LogicContext> remove(String contextId);
	
	void callback(String contextId, Object result);
	
	void setContextId(String contextId);
	
	void unsetContextId();
	
	String getContextId();
}
