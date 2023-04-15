package io.linkedlogics.service;

import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.context.Context;

public interface CallbackService extends LinkedLogicsService {
	void set(String contextId, LinkedLogicsCallback callback);
	
	void publish(Context context);
}
