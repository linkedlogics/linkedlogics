package dev.linkedlogics.service.local;

import java.util.Map;

import dev.linkedlogics.service.EvaluatorService;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalEvaluatorService implements EvaluatorService {
	
	@Override
	public Object evaluate(String expression, String id, Map<String, Object> params) {
		Binding binding = new Binding();
		params.entrySet().forEach(e -> {
			binding.setVariable(e.getKey(), e.getValue());
		});
		GroovyShell groovyShell = new GroovyShell(binding);
		return groovyShell.evaluate(expression);
	}
}
