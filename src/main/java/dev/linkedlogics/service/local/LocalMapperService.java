package dev.linkedlogics.service.local;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.service.MapperService;

public class LocalMapperService implements MapperService {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Override
	public <T> T mapFrom(String string, Class<T> objectClass) {
		try {
			return (T) mapper.readValue(string, objectClass);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String mapTo(Object object) {
		try {
			return mapper.writeValueAsString(object);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

}
