package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.LinkedLogicsCallback;
import dev.linkedlogics.context.Context;

public interface CallbackService extends LinkedLogicsService {
	void set(String contextId, LinkedLogicsCallback callback);
	
	void publish(Context context);
}
