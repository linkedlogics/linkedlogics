package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.context.LogicContext;

public interface AsyncService extends LinkedLogicsService {
	
	void set(LogicContext context);
	
	Optional<LogicContext> remove(String contextId);
	
	void asyncCallback(String contextId, Object result);
	
	void asyncCallerror(String contextId, Throwable error);
	
	void setContextId(String contextId);
	
	void unsetContextId();
	
	String getContextId();
}
