package io.linkedlogics.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public interface MapperService extends LinkedLogicsService {

	public ObjectMapper getMapper() ;
	
	@SuppressWarnings("unchecked")
	default <T> T clone(T object) throws JsonProcessingException {
		return (T) getMapper().readValue(getMapper().writeValueAsString(object), object.getClass());
	}
}
