package dev.linkedlogics;

import java.util.Map;

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
		return ServiceLocator.getInstance().getProcessService().getProcess(processId, version).map(p -> {
			Context context = new Context(p.getId(), p.getVersion(), params);
			ServiceLocator.getInstance().getContextService().set(context);
			
			LogicContext logicContext = new LogicContext();
			logicContext.setId(context.getId());
			
			ServiceLocator.getInstance().getProcessorService().process(new StartTask(logicContext));
			return context.getId();
		}).orElseThrow(() -> new IllegalArgumentException(String.format("process %s[%d] is not found", processId, version)));
	}
	
	public static String start(String processId, Map<String, Object> params) {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId).map(p -> {
			Context context = new Context(p.getId(), p.getVersion(), params);
			ServiceLocator.getInstance().getContextService().set(context);
			
			LogicContext logicContext = new LogicContext();
			logicContext.setId(context.getId());
			
			ServiceLocator.getInstance().getProcessorService().process(new StartTask(logicContext));
			return context.getId();
		}).orElseThrow(() -> new IllegalArgumentException(String.format("process %s is not found", processId)));
	}
	
	public static void callback(String contextId, Object result) {
		ServiceLocator.getInstance().getCallbackService().callback(contextId, result);
	}
	
	public static String getContextId() {
		return ServiceLocator.getInstance().getCallbackService().getContextId();
	}
}
