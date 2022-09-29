package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.context.Context;

public interface ContextService extends LinkedLogicsService {
	
	boolean set(Context context);

	Optional<Context> get(String contextId);
	
	Optional<Context> remove(String contextId);
}
