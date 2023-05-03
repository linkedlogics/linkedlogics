package io.linkedlogics;

import java.util.Map;
import java.util.Optional;

import io.linkedlogics.config.LinkedLogicsConfiguration;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.Status;
import io.linkedlogics.service.ServiceConfigurer;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;
import io.linkedlogics.service.task.CancelTask;
import io.linkedlogics.service.task.StartTask;

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
	
	public static String start(Context context) {
		return start(context, null);
	}
	
	public static String start(Context context, LinkedLogicsCallback callback) {
		ServiceLocator.getInstance().getContextService().set(context);
		Optional.ofNullable(callback).ifPresent(c -> ServiceLocator.getInstance().getCallbackService().set(context.getId(), c));
		ServiceLocator.getInstance().getProcessorService().process(new StartTask(context));
		return context.getId();
	}
	
	public static void cancel(String contextId) {
		ServiceLocator.getInstance().getContextService().get(contextId).ifPresent(c -> {
			ServiceLocator.getInstance().getProcessorService().process(new CancelTask(c));
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
