package dev.linkedlogics.service;

import dev.linkedlogics.context.Context;

public interface ConsumerService extends LinkedLogicsService {
	
	void consume(Context context);
}
