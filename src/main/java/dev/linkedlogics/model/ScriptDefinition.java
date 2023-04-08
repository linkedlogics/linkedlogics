package dev.linkedlogics.model;

import java.lang.reflect.Method;

import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.model.parameter.Parameter;
import lombok.Getter;

@Getter
public class ScriptDefinition {
	private final String id;
	private final int version;
	private final String description;
	
	private final Method method;
	private final Object object;
	
	private final Parameter[] parameters;
	private final String returnAs;
	private boolean isReturnAsync;
	private boolean isReturnMap;
	
	public ScriptDefinition(Logic annotation, Method method, Object object) {
		this.id = annotation.id();
		this.version = annotation.version();
		this.description = annotation.description();
		this.isReturnAsync = annotation.returnAsync();
		this.isReturnMap = annotation.returnMap();
		this.method = method;
		this.object = object;
		this.parameters = Parameter.initParameters(method);
		this.returnAs = !annotation.returnAs().isEmpty() ? annotation.returnAs() : null;
	}
}
