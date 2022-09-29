package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.model.ProcessDefinition;

public interface ProcessService extends LinkedLogicsService {
	
	Optional<ProcessDefinition> getProcess(String processId);
	
	Optional<ProcessDefinition> getProcess(String processId, int version);
	
	void registerObject(Object processObject);
	
	void registerClass(Class processClass);
}
