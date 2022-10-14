package dev.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;

public class LocalContextService implements ContextService {
	private ConcurrentHashMap<String, String> contextMap = new ConcurrentHashMap<>();
	
	@Override
	public void set(Context context) {
		contextMap.put(context.getId(), toString(context));
	}

	@Override
	public Optional<Context> get(String contextId) {
		return Optional.ofNullable(fromString(contextMap.get(contextId)));
	}

	@Override
	public Optional<Context> remove(String contextId) {
		return Optional.ofNullable(fromString(contextMap.remove(contextId)));
	}
	
	private String toString(Context context) {
		String s = ServiceLocator.getInstance().getMapperService().mapTo(context);
//		System.out.println("SET -> " + s);
		return s;
	}
	
	private Context fromString(String string) {
		if (string == null || string.length() == 0) {
			return null;
		}
//		System.out.println("GET <- " + string);
		return ServiceLocator.getInstance().getMapperService().mapFrom(string, Context.class);
	}
}
