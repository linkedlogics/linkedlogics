package dev.linkedlogics.model.process.helper;

import dev.linkedlogics.exception.CycleDetectedError;
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
			System.out.println("\tsettting logics for " + p.getProcessId() + " v" + p.getVersion() + "in " + definition.getId() + " v" + definition.getVersion());
			if (p.getProcessId().equals(definition.getId()) && p.getVersion() == definition.getVersion()) {
				throw new CycleDetectedError(definition.getId(), definition.getVersion());
			}
			System.out.println("XXX");
			p.setLogics(ServiceLocator.getInstance().getProcessService().getProcess(p.getProcessId(), p.getVersion()).get().cloneLogics());
			p.getLogics().forEach(l -> {
				p.getInputMap().entrySet().forEach(e -> {
					l.getInputMap().putIfAbsent(e.getKey(), e.getValue());
				});
			});
			LogicPositioner.setPositions(definition);
		});
	}
}
