package io.linkedlogics.service;

import java.lang.reflect.Proxy;

import io.linkedlogics.service.config.ConfigInvocationHandler;
import lombok.Getter;

public abstract class ConfigurableService<T> {
	@Getter
	private T config;
	
	public ConfigurableService(Class<T> configClass) {
		try {
			config = (T) Proxy.newProxyInstance(configClass.getClassLoader(), new Class[] {configClass}, new ConfigInvocationHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
