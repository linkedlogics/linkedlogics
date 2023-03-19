package dev.linkedlogics.service;

import dev.linkedlogics.context.Context;

public interface LoggerService extends LinkedLogicsService {
	
	void log(Context context);
	
	void logError(Context context);
	
	void logContext(Context context);
}
