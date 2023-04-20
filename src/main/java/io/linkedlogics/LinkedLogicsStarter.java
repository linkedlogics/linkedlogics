package io.linkedlogics;

import java.util.Map;

import io.linkedlogics.context.Context;
import io.linkedlogics.exception.MissingLogicError;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.ExpressionLogicDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.model.process.helper.LogicFinder;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.task.StartTask;

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
		
		if (!process.getInputs().isEmpty()) {
			final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
			process.getInputs().entrySet().stream().forEach(e -> {
				if (e.getValue() instanceof ExpressionLogicDefinition) {
					context.getParams().put(e.getKey(), evaluator.evaluate(((ExpressionLogicDefinition) e.getValue()).getExpression(), context.getParams()));
				} else {
					context.getParams().put(e.getKey(), e.getValue());
				}
			});
		}
		ServiceLocator.getInstance().getContextService().set(context);
		
		if (callback != null) {
			ServiceLocator.getInstance().getCallbackService().set(context.getId(), callback);
		}
		
		ServiceLocator.getInstance().getProcessorService().process(new StartTask(context));
		return context.getId();
	}
}