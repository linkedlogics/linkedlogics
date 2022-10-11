package dev.linkedlogics;

import java.util.Map;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.task.StartTask;

class LinkedLogicsStarter {
	public static String start(String processId, Map<String, Object> params, LinkedLogicsCallback callback) {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId).map(p -> {
			return start(p, params, callback);
		}).orElseThrow(() -> new IllegalArgumentException(String.format("process %s is not found", processId)));
	}
	
	public static String start(String processId, int processVersion, Map<String, Object> params, LinkedLogicsCallback callback) {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId, processVersion).map(p -> {
			return start(p, params, callback);
		}).orElseThrow(() -> new IllegalArgumentException(String.format("process %s is not found", processId)));
	}
	
	private static String start(ProcessDefinition process, Map<String, Object> params, LinkedLogicsCallback callback) {
		Context context = new Context(process.getId(), process.getVersion(), params);

		ServiceLocator.getInstance().getCallbackService().set(context.getId(), callback);
		if (callback != null) {
			ServiceLocator.getInstance().getContextService().set(context);
		}
		
		LogicContext logicContext = new LogicContext();
		logicContext.setId(context.getId());
		
		ServiceLocator.getInstance().getProcessorService().process(new StartTask(logicContext));
		return context.getId();
	}
}
