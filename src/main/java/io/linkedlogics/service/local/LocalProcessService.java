package io.linkedlogics.service.local;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.concurrent.ConcurrentHashMap;

import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.ProcessDefinitionReader;
import io.linkedlogics.model.ProcessDefinitionWriter;
import io.linkedlogics.model.process.helper.LogicDependencies;
import io.linkedlogics.service.ProcessService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalProcessService implements ProcessService {
	protected final Map<String, ProcessDefinition> definitions = new ConcurrentHashMap<>();
	
	@Override
	public Optional<ProcessDefinition> getProcess(String processId) {
		return getProcess(processId, LATEST_VERSION);
	}

	@Override
	public Optional<ProcessDefinition> getProcess(String processId, int version) {
		if (version == LATEST_VERSION) {
			OptionalInt latestVersion = definitions.values().stream().filter(p -> p.getId().equals(processId)).map(p -> p.getVersion()).mapToInt(i -> i).max();
			if (latestVersion.isPresent()) {
				return Optional.ofNullable(definitions.get(getProcessKey(processId, latestVersion.getAsInt())));
			}
		}
		return Optional.ofNullable(definitions.get(getProcessKey(processId, version)));
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
		.filter(m -> ProcessDefinition.class.isAssignableFrom(m.getReturnType()))
		.map(m -> {
			try {
				return (ProcessDefinition) m.invoke(null);
			} catch (InvocationTargetException e) {
				throw new RuntimeException(e.getCause());
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
		}).forEach(this::addProcess);
	}
	
	protected void addProcess(ProcessDefinition process) {
		if (process.isArchived()) {
			log.info(String.format("process %s:%d is archived", process.getId(), process.getVersion()));
			return;
		}
		
		ProcessDefinition validatedDefinition = new ProcessDefinitionReader(new ProcessDefinitionWriter(process).write()).read();
		
		if (definitions.containsKey(getProcessKey(validatedDefinition))) {
			log.warn("process {}:{} was overwritten", process.getId(), process.getVersion());
		}
		
		definitions.put(getProcessKey(validatedDefinition), validatedDefinition);
		definitions.values()
			.stream()
			.sorted((p1, p2) -> p1.compareTo(p2))
			.forEach(LogicDependencies::setDependencies);
	}
	
	protected String getProcessKey(ProcessDefinition process) {
		return String.format("%s:%d", process.getId(), process.getVersion());
	}
	
	protected String getProcessKey(String processId, int version) {
		return String.format("%s:%d", processId, version);
	}
}