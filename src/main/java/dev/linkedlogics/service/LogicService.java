package dev.linkedlogics.service;

import java.util.Optional;

import dev.linkedlogics.model.LogicDefinition;

public interface LogicService extends LinkedLogicsService {
	public static final int LATEST_VERSION = -1;
	
	Optional<LogicDefinition> getLogic(String logicId);
	
	Optional<LogicDefinition> getLogic(String logicId, int version);
	
	void registerObject(Object logicObject);
	
	void registerClass(Class logicClass);
}
