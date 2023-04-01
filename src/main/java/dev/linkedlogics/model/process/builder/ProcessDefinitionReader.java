package dev.linkedlogics.model.process.builder;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;

import dev.linkedlogics.model.ProcessDefinition;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessDefinitionReader {
	private String process;

	public ProcessDefinition read() {
		Binding binding = new Binding();
		GroovyShell groovyShell = new GroovyShell(binding);
		Object result = groovyShell.evaluate(process);
		return (ProcessDefinition) result;
	}
	
	public static void main(String[] args) {
		String s = new ProcessDefinitionWriter(scenario1()).write();
		System.out.println(s);
		
		ProcessDefinition p = new ProcessDefinitionReader(s).read();
		System.out.println(p.getId());
		System.out.println(p.getLogics().size());
	}
	
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.build();
	}
}
