package dev.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.ContextService;

public class LocalContextService implements ContextService {
	private ConcurrentHashMap<String, Context> contextMap = new ConcurrentHashMap<>();
	
	@Override
	public void set(Context context) {
		contextMap.put(context.getId(), context);
	}

	@Override
	public Optional<Context> get(String contextId) {
		return Optional.ofNullable(contextMap.get(contextId));
	}

	@Override
	public Optional<Context> remove(String contextId) {
		return Optional.ofNullable(contextMap.remove(contextId));
	}
}
