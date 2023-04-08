package dev.linkedlogics.service.local;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.script;
import static dev.linkedlogics.LinkedLogicsBuilder.text;
import static dev.linkedlogics.LinkedLogicsBuilder.var;

import java.util.Map;

import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.ProcessDefinitionWriter;
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
	
	public static void main(String[] args) {
		String script = "import groovy.json.JsonSlurper \ndef jsonString = '{\"name\": \"John Doe\", \"age\": 30, \"address\": {\"city\": \"New York\", \"state\": \"NY\"}}' \ndef json = new JsonSlurper().parseText(jsonString)\nreturn json";
		
		ProcessDefinition p = createProcess("SIMPLE_SCENARIO_1", 0)
				.add(script(text(script)).returnAsMap().build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("name")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("address.city")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", var("address.state")).build())
				.add(script(text("result = list.size() + ' items'")).returnAs("text").build())
				.build();
		
							  createProcess("SIMPLE_SCENARIO_1", 0)    
			    .add(script(text("import groovy.json.JsonSlurper \ndef jsonString = '{\"name\": \"John Doe\", \"age\": 30, \"address\": {\"city\": \"New York\", \"state\": \"NY\"}}' \ndef json = new JsonSlurper().parseText(jsonString)\nreturn json")).build())
			    .add(logic("INSERT").inputs("val", expr("name"), "list", expr("list")).build())
			    .add(logic("INSERT").inputs("val", expr("address.city"), "list", expr("list")).build())
			    .add(logic("INSERT").inputs("val", expr("address.state"), "list", expr("list")).build())
			    .add(script(text("result = list.size() + ' items'")).returnAs("text").build())
	.build();

		
		System.out.println(new ProcessDefinitionWriter(p).write());
	}
}
