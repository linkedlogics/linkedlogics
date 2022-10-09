package dev.linkedlogics.service.local;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.exception.AlreadyExistingError;
import dev.linkedlogics.model.LogicDefinition;
import dev.linkedlogics.service.LogicService;

public class LocalLogicService implements LogicService {
	private final Map<String, Map<Integer, LogicDefinition>> definitions = new HashMap<>();
	
	@Override
	public Optional<LogicDefinition> getLogic(String logicId) {
		return getLogic(logicId, LATEST_VERSION);
	}

	@Override
	public Optional<LogicDefinition> getLogic(String logicId, int version) {
		if (version == LATEST_VERSION) {
			return Optional.ofNullable(definitions.get(logicId)).map(m -> m.get(m.keySet().stream().mapToInt(i -> i).max().getAsInt()));
		}
		return Optional.ofNullable(definitions.get(logicId)).map(m -> m.get(version));
	}
	
	@Override
	public void register(Object logics) {
		if (logics.getClass().equals(Class.class)) {
			registerClass((Class<?>) logics);
		} else {
			registerObject(logics);
		}
	}

	protected void registerObject(Object logicObject) {
		Arrays.stream(logicObject.getClass().getDeclaredMethods())
		.filter(m -> m.getAnnotation(Logic.class) != null)
		.map(m -> new LogicDefinition(m.getAnnotation(Logic.class), m, logicObject))
		.forEach(this::addLogic);
	}

	protected void registerClass(Class<?> logicClass) {
		Arrays.stream(logicClass.getDeclaredMethods())
		.filter(m -> Modifier.isStatic(m.getModifiers()))
		.filter(m -> m.getAnnotation(Logic.class) != null)
		.map(m -> new LogicDefinition(m.getAnnotation(Logic.class), m, null))
		.forEach(this::addLogic);
	}
	
	protected void addLogic(LogicDefinition definition) throws AlreadyExistingError {
		if (!definitions.containsKey(definition.getId())) {
			definitions.put(definition.getId(), new HashMap<>() {{
				put(definition.getVersion(), definition);
			}});
		} else {
			Map<Integer, LogicDefinition> versionMap = definitions.get(definition.getId());
			
			if (!versionMap.containsKey(definition.getVersion())) {
				versionMap.put(definition.getVersion(), definition);
			} else {
				throw new AlreadyExistingError(definition.getId(), definition.getVersion(), "LOGIC");	
			}
		}
	}
}
