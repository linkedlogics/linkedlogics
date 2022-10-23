package dev.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
