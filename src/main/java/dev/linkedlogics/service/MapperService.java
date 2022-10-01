package dev.linkedlogics.service;

public interface MapperService extends LinkedLogicsService {

	public <T> T mapFrom(String object, Class<T> objectClass) ;

	public String mapTo(Object object) ;
}
