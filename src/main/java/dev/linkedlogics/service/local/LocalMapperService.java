package dev.linkedlogics.service.local;

import java.text.SimpleDateFormat;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dev.linkedlogics.service.MapperService;

public class LocalMapperService implements MapperService {

	private ObjectMapper mapper;
	
	@Override
	public void start() {
		mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH24:mm:ss.SZ"));
	}
	
	@Override
	public ObjectMapper getMapper() {
		return mapper;
	}
}
