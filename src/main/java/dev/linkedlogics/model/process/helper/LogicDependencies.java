package dev.linkedlogics.model.process.helper;

import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.model.process.ProcessLogicDefinition;
import dev.linkedlogics.service.ServiceLocator;

public class LogicDependencies {
	
	public static void setDependencies(ProcessDefinition definition) {
		LogicFinder.findLogics(definition, ProcessLogicDefinition.class).stream()
		.map(l -> (ProcessLogicDefinition) l)
		.filter(p -> p.getLogics().isEmpty())
		.filter(p -> ServiceLocator.getInstance().getProcessService().getProcess(p.getProcessId(), p.getVersion()).isPresent())
		.forEach(p -> {
			p.setLogics(ServiceLocator.getInstance().getProcessService().getProcess(p.getProcessId(), p.getVersion()).get().cloneLogics());
			p.getLogics().forEach(l -> {
				p.getInputs().entrySet().forEach(e -> {
					l.getInputs().putIfAbsent(e.getKey(), e.getValue());
				});
			});
			LogicPositioner.setPositions(definition);
		});
	}
}
