package dev.linkedlogics.process.parser;

import static dev.linkedlogics.LinkedLogicsBuilder.branch;
import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.error;
import static dev.linkedlogics.LinkedLogicsBuilder.exit;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.fail;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.jump;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.max;
import static dev.linkedlogics.LinkedLogicsBuilder.process;
import static dev.linkedlogics.LinkedLogicsBuilder.retry;
import static dev.linkedlogics.LinkedLogicsBuilder.savepoint;
import static dev.linkedlogics.LinkedLogicsBuilder.seconds;
import static dev.linkedlogics.LinkedLogicsBuilder.verify;
import static dev.linkedlogics.LinkedLogicsBuilder.when;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.ProcessDefinitionReader;
import dev.linkedlogics.model.ProcessDefinitionWriter;
import dev.linkedlogics.process.SimpleProcess1Tests;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class ProcessReaderWriterTest {
	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(SimpleProcess1Tests.class);
		LinkedLogics.registerProcess(SimpleProcess1Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		ProcessDefinition scenario = scenario1();
		
		String def = new ProcessDefinitionWriter(scenario).write();
		assertThat(def).isNotNull();
		
		ProcessDefinition process = new ProcessDefinitionReader(def).read();
		assertThat(process).isNotNull();
		assertThat(process.getId()).isEqualTo(scenario.getId());
		assertThat(process.getVersion()).isEqualTo(scenario.getVersion());
		assertThat(process.getLogics().size()).isEqualTo(scenario.getLogics().size());
	}

	@Test
	public void testScenario2() {
		ProcessDefinition scenario = scenario2();
		
		String def = new ProcessDefinitionWriter(scenario).write();
		assertThat(def).isNotNull();
		
		ProcessDefinition process = new ProcessDefinitionReader(def).read();
		assertThat(process).isNotNull();
		assertThat(process.getId()).isEqualTo(scenario.getId());
		assertThat(process.getVersion()).isEqualTo(scenario.getVersion());
		assertThat(process.getLogics().size()).isEqualTo(scenario.getLogics().size());
	}
	
	@Test
	public void testScenario3() {
		ProcessDefinition scenario = scenario3();
		
		String def = new ProcessDefinitionWriter(scenario).write();
		assertThat(def).isNotNull();
		
		ProcessDefinition process = new ProcessDefinitionReader(def).read();
		assertThat(process).isNotNull();
		assertThat(process.getId()).isEqualTo(scenario.getId());
		assertThat(process.getVersion()).isEqualTo(scenario.getVersion());
		assertThat(process.getLogics().size()).isEqualTo(scenario.getLogics().size());
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.build();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(group(
				branch(expr("a > 5"), 
						logic("INSERT")
						.input("list", expr("list")).input("val", "v1").input("number", 2020)
						.delayed(5)
						.handle(error().withCodes(100,101).orMessages("error1", "error2").usingLogic(logic("ERROR").build()).build())
						.fork("F1")
						.join("F2")
						.label("L1")
						.retry(retry(5, 3).includeCodes(100).andMessages("error1").build())
						.timeout(5)
						.build()).build(),
					exit().build(),
					fail().withCode(200).andMessage("failure").build(),
					jump("L1").build(),
					savepoint().build(),
					verify(expr("false")).elseFailWithCode(100).andMessage("failure").build(),
					process("SIMPLE_SCENARIO_1", 0).build()
				).build()).build();

	}
	
	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_2", 0).input("list", expr("list")).input("val", "v1").input("number", 2020)
				.add(logic("INSERT").inputs("list", expr("list"), "val", "v1").build())
				.add(logic("INSERT").inputs("list", expr("list"), "val", "v1")
						.retry(retry(max(5), seconds(3)).includeCodes(100,234).andMessages("error1","dfgdg").build())
						.delayed(5)
						.fork("F1")
						.join("F0")
						.timeout(5)
						.label("L1")
						.handle(error().withCodes(100,101).orMessages("error1", "error2").usingLogic(logic("ERROR").build()).build())
						.build())
				.add(exit().build())
				.add(fail().withCode(200).andMessage("failure").build())
				.add(branch(when("account == 222"), logic("INSERT").inputs("list", expr("list"), "val", "v1").build()).build())
				.add(group(
					branch(expr("a > 5"), 
							logic("INSERT")
							.input("list", expr("list")).input("val", "v1").input("number", 2020)
							.delayed(5)
							.handle(error().withCodes(100,101).orMessages("error1", "error2").usingLogic(logic("ERROR").build()).build())
							.fork("F1")
							.join("F2")
							.label("L1")
							.retry(retry(max(5), seconds(3)).includeCodes(100).andMessages("error1").build())
							.timeout(5)
							.build()).build(),
					exit().build(),
					fail().withCode(200).andMessage("failure").build(),
					jump("L1").build(),
					savepoint().build(),
					verify(expr("false")).elseFailWithCode(100).andMessage("failure").build()
					).build())
				.build();
	}
}
