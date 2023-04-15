package io.linkedlogics.service.local;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

import io.linkedlogics.annotation.Logic;
import io.linkedlogics.exception.AlreadyExistingError;
import io.linkedlogics.model.LogicDefinition;
import io.linkedlogics.service.LogicService;

public class LocalLogicService implements LogicService {
	private final Map<String, LogicDefinition> definitions = new ConcurrentHashMap<>();

	@Override
	public Optional<LogicDefinition> getLogic(String logicId) {
		return getLogic(logicId, LATEST_VERSION);
	}

	@Override
	public Optional<LogicDefinition> getLogic(String logicId, int version) {
		if (version == LATEST_VERSION) {
			OptionalInt latestVersion = definitions.values().stream().filter(p -> p.getId().equals(logicId)).map(p -> p.getVersion()).mapToInt(i -> i).max();
			if (latestVersion.isPresent()) {
				return Optional.ofNullable(definitions.get(getLogicKey(logicId, latestVersion.getAsInt())));
			}
		}
		return Optional.ofNullable(definitions.get(getLogicKey(logicId, version)));
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
		if (definitions.containsKey(getLogicKey(definition))) {
			throw new AlreadyExistingError(definition.getId(), definition.getVersion(), "LOGIC");	
		}

		definitions.put(getLogicKey(definition), definition);
	}

	protected String getLogicKey(LogicDefinition logic) {
		return String.format("%s:%d", logic.getId(), logic.getVersion());
	}

	protected String getLogicKey(String logicId, int version) {
		return String.format("%s:%d", logicId, version);
	}
}
