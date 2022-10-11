package dev.linkedlogics;

import java.util.Map;
import java.util.function.Consumer;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.ServiceConfigurer;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;
import dev.linkedlogics.service.task.StartTask;

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
	
	public static void asyncCallback(String contextId, Object result) {
		ServiceLocator.getInstance().getAsyncService().asyncCallback(contextId, result);
	}
	
	public static String getContextId() {
		return ServiceLocator.getInstance().getAsyncService().getContextId();
	}
}
