package io.linkedlogics.service.local;

import java.util.Map;
import java.util.Optional;

import org.codehaus.groovy.control.CompilationFailedException;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import io.linkedlogics.service.EvaluatorService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalEvaluatorService implements EvaluatorService {
	
	@Override
	public Object evaluate(String expression, Map<String, Object> params) {
		Binding binding = new Binding();
		params.entrySet().forEach(e -> {
			binding.setVariable(e.getKey(), e.getValue());
		});
		GroovyShell groovyShell = new GroovyShell(binding);
		return groovyShell.evaluate(expression);
	}

	@Override
	public Optional<String> checkSyntax(String expression) {
		try {
			GroovyShell groovyShell = new GroovyShell();
			groovyShell.parse(expression);
			return Optional.empty();
		} catch (CompilationFailedException e) {
			return Optional.of(e.getMessage());
		}
	}
}
