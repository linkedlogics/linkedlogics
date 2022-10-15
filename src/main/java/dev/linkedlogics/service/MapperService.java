package dev.linkedlogics.service;

import java.util.Map;

public interface MapperService extends LinkedLogicsService {

	public <T> T mapFrom(String object, Class<T> objectClass);

	public String mapTo(Object object);
	
	public <T> T convertFrom(Object object, Class<T> targetClass);
}
