package io.linkedlogics;

import java.util.Map;

import io.linkedlogics.config.LinkedLogicsConfiguration;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.Status;
import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class LinkedLogics {
	static {
		configure(new LocalServiceConfigurer());
	}
	
	public static void configure(ServiceConfigurer configurer) {
		ServiceLocator.getInstance().configure(configurer);
	}
	
	public static void registerLogic(Object logic) {
		ServiceLocator.getInstance().getLogicService().register(logic);
	}
	
	public static void registerProcess(Object process) {
		ServiceLocator.getInstance().getProcessService().register(process);
	}
	
	public static void shutdown() {
		ServiceLocator.getInstance().shutdown();
	}
	
	public static void launch() {
		ServiceLocator.getInstance().start();
	}
	
	public static String start(String processId, int version, Map<String, Object> params) {
		return LinkedLogicsStarter.start(processId, version, params, null);
	}
	
	public static String start(String processId, Map<String, Object> params) {
		return LinkedLogicsStarter.start(processId, params, null);
	}
	
	public static String start(String processId, int version, Map<String, Object> params, LinkedLogicsCallback callback) {
		return LinkedLogicsStarter.start(processId, version, params, callback);
	}
	
	public static String start(String processId, Map<String, Object> params, LinkedLogicsCallback callback) {
		return LinkedLogicsStarter.start(processId, params, callback);
	}
	
	public static void cancel(String contextId) {
		ServiceLocator.getInstance().getContextService().get(contextId).ifPresent(c -> {
			c.setStatus(Status.CANCELLED);
			ServiceLocator.getInstance().getCallbackService().publish(c);
			ServiceLocator.getInstance().getContextService().remove(contextId);
		});
		
	}
	
	public static void asyncCallback(String contextId, Object result) {
		ServiceLocator.getInstance().getAsyncService().asyncCallback(contextId, result);
	}
	
	public static void asyncCallback(String contextId, Throwable error) {
		ServiceLocator.getInstance().getAsyncService().asyncCallerror(contextId, error);
	}
	
	public static String getContextId() {
		return ServiceLocator.getInstance().getAsyncService().getContextId();
	}
	
	public static String getApplicationName() {
		return LinkedLogicsConfiguration.getConfig(LinkedLogicsConfiguration.APPLICATION_NAME).map(c -> c.toString()).orElseThrow(() -> new NullPointerException("missing application name"));
	}
}
