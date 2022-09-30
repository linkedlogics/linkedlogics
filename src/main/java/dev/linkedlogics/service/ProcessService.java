package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.model.process.ProcessDefinition;

public interface ProcessService extends LinkedLogicsService {
	public static final int LATEST_VERSION = -1;
	
	Optional<ProcessDefinition> getProcess(String processId);
	
	Optional<ProcessDefinition> getProcess(String processId, int version);
	
	void register(Object processes);
}
