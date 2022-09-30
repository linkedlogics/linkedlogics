package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.context.Context;

public interface ContextService extends LinkedLogicsService {
	
	void set(Context context);

	Optional<Context> get(String contextId);
	
	Optional<Context> remove(String contextId);
}
