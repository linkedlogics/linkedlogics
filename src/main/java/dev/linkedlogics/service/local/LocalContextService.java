package dev.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.exception.ContextAlreadyUpdatedException;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;

public class LocalContextService implements ContextService {
	private ConcurrentHashMap<String, String> contextMap = new ConcurrentHashMap<>();
	private ConcurrentHashMap<String, AtomicInteger> versionMap = new ConcurrentHashMap<>();
	
	@Override
	public void set(Context context) {
		AtomicInteger version = versionMap.getOrDefault(context.getId(), new AtomicInteger(context.getVersion()));

		if (version.compareAndSet(context.getVersion(), context.getVersion() + 1)) {
			context.setVersion(context.getVersion() + 1);
			contextMap.put(context.getId(), toString(context));
			versionMap.put(context.getId(), version);
		} else {
			throw new ContextAlreadyUpdatedException(context.getId());
		}
	}

	@Override
	public Optional<Context> get(String contextId) {
		return Optional.ofNullable(fromString(contextMap.get(contextId)));
	}

	@Override
	public Optional<Context> remove(String contextId) {
		versionMap.remove(contextId);
		return Optional.ofNullable(fromString(contextMap.remove(contextId)));
	}
	
	private String toString(Context context) {
		try {
			return ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Context fromString(String string) {
		if (string == null || string.length() == 0) {
			return null;
		}
		
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		try {
			return mapper.readValue(string, Context.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
