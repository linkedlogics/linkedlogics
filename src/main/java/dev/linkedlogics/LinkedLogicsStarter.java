package dev.linkedlogics;

import java.util.Map;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.exception.MissingLogicError;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.model.process.ProcessLogicDefinition;
import dev.linkedlogics.model.process.helper.LogicFinder;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.task.StartTask;

class LinkedLogicsStarter {
	static String start(String processId, Map<String, Object> params, LinkedLogicsCallback callback) {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId).map(p -> {
			return start(p, params, callback);
		}).orElseThrow(() -> new IllegalArgumentException(String.format("process %s is not found", processId)));
	}
	
	static String start(String processId, int processVersion, Map<String, Object> params, LinkedLogicsCallback callback) {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId, processVersion).map(p -> {
			return start(p, params, callback);
		}).orElseThrow(() -> new IllegalArgumentException(String.format("process %s is not found", processId)));
	}
	
	private static String start(ProcessDefinition process, Map<String, Object> params, LinkedLogicsCallback callback) {
		Context context = new Context(process.getId(), process.getVersion(), params);
		
		LogicFinder.findLogics(process, ProcessLogicDefinition.class).stream()
			.map(l -> (ProcessLogicDefinition) l)
			.filter(p -> p.getLogics().isEmpty())
			.findAny().map(l -> {
				throw new MissingLogicError(l.getProcessId(), l.getVersion());
			});
		
		ServiceLocator.getInstance().getContextService().set(context);
		
		if (callback != null) {
			ServiceLocator.getInstance().getCallbackService().set(context.getId(), callback);
		}
		
		LogicContext logicContext = new LogicContext();
		logicContext.setId(context.getId());
		
		ServiceLocator.getInstance().getProcessorService().process(new StartTask(logicContext));
		return context.getId();
	}
}
