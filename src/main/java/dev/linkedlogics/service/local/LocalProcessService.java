package dev.linkedlogics.service.local;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.linkedlogics.annotation.ProcessChain;
import dev.linkedlogics.exception.AlreadyExistingError;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.service.ProcessService;

public class LocalProcessService implements ProcessService {
private final Map<String, Map<Integer, ProcessDefinition>> definitions = new HashMap<>();
	
	@Override
	public Optional<ProcessDefinition> getProcess(String processId) {
		return getProcess(processId, LATEST_VERSION);
	}

	@Override
	public Optional<ProcessDefinition> getProcess(String processId, int version) {
		if (version == LATEST_VERSION) {
			return Optional.ofNullable(definitions.get(processId)).map(m -> m.get(m.keySet().stream().mapToInt(i -> i).max().getAsInt()));
		}
		return Optional.ofNullable(definitions.get(processId)).map(m -> m.get(version));
	}
	
	@Override
	public void register(Object logics) {
		if (logics.getClass().equals(Class.class)) {
			registerClass((Class<?>) logics);
		} else {
			registerObject(logics);
		}
	}
	
	protected void registerObject(Object processObject) {
		Arrays.stream(processObject.getClass().getDeclaredMethods())
		.filter(m -> m.getAnnotation(ProcessChain.class) != null)
		.filter(m -> ProcessDefinition.class.isAssignableFrom(m.getReturnType()))
		.map(m -> {
			try {
				return (ProcessDefinition) m.invoke(processObject);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}).forEach(this::addProcess);
	}
	
	protected void registerClass(Class<?> processClass) {
		Arrays.stream(processClass.getDeclaredMethods())
		.filter(m -> Modifier.isStatic(m.getModifiers()))
		.filter(m -> m.getAnnotation(ProcessChain.class) != null)
		.filter(m -> ProcessDefinition.class.isAssignableFrom(m.getReturnType()))
		.map(m -> {
			try {
				return (ProcessDefinition) m.invoke(null);
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}).forEach(this::addProcess);
	}
	
	protected void addProcess(ProcessDefinition definition) {
		if (!definitions.containsKey(definition.getId())) {
			definitions.put(definition.getId(), new HashMap<>() {{
				put(definition.getVersion(), definition);
			}});
		} else {
			Map<Integer, ProcessDefinition> versionMap = definitions.get(definition.getId());
			
			if (!versionMap.containsKey(definition.getVersion())) {
				versionMap.put(definition.getVersion(), definition);
			} else {
				throw new AlreadyExistingError(definition.getId(), definition.getVersion(), "PROCESS");	
			}
		}
	}
}
