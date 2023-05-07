package io.linkedlogics.service;

import java.lang.reflect.Proxy;

import io.linkedlogics.service.config.ConfigInvocationHandler;
import io.linkedlogics.service.config.ServiceConfig;
import lombok.Getter;

public abstract class ConfigurableService<T extends ServiceConfig> {
	@Getter
	private T config;
	
	public ConfigurableService(Class<? extends ServiceConfig> configClass) {
		try {
			config = (T) Proxy.newProxyInstance(configClass.getClassLoader(), new Class[] {configClass}, new ConfigInvocationHandler());
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
