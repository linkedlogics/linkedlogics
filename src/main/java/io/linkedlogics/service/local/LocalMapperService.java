package io.linkedlogics.service.local;

import java.text.SimpleDateFormat;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import io.linkedlogics.service.MapperService;

public class LocalMapperService implements MapperService {

	private ObjectMapper mapper;
	
	public LocalMapperService() {
		mapper = new ObjectMapper();
		mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
		mapper.registerModule(new JavaTimeModule());
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH24:mm:ss.SZ"));
	}
	
	@Override
	public ObjectMapper getMapper() {
		return mapper;
	}
}