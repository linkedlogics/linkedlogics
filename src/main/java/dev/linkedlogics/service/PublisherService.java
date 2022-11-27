package dev.linkedlogics.service;

import dev.linkedlogics.context.Context;

public interface PublisherService extends LinkedLogicsService {
	
	void publish(Context context);
}
