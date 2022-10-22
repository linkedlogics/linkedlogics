package dev.linkedlogics.service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public class ServiceConfigurer {
	protected Map<Class<?>, LinkedLogicsService> services = new HashMap<>();
	
	protected Map<Class<?>, LinkedLogicsService> getServices() {
		return services;
	}
	
	public ServiceConfigurer configure(LinkedLogicsService service) {
		getLinkedInterfaces(service).forEach(c -> services.put(c, service));
		services.put(service.getClass(), service);
		return this;
	}
	
	public ServiceConfigurer configure(ServiceConfigurer configurer) {
		this.services.putAll(configurer.getServices());
		return this;
	}
	
	public Stream<Class<?>> getLinkedInterfaces(LinkedLogicsService service) {
		return Arrays.stream(service.getClass().getInterfaces()).filter(i -> i == LinkedLogicsService.class || LinkedLogicsService.class.isAssignableFrom(i));
	}
}
