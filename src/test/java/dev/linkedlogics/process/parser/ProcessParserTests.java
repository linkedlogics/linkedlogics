package dev.linkedlogics.process.parser;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.*;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.process.JumpProcess1Tests;
import dev.linkedlogics.process.SimpleProcess1Tests;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class ProcessParserTests {
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
		ObjectMapper mapper = new ObjectMapper();
		 mapper.setSerializationInclusion(Include.NON_NULL);
		 mapper.setSerializationInclusion(Include.NON_EMPTY);
		try {
			System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(scenario2()));
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
						.handle(error().errorCodeSet(100,101).errorMessageSet("error1", "error2").errorLogic(logic("ERROR").build()).build())
						.fork("F1")
						.join("F2")
						.label("L1")
						.retry(retry(5, 3).errorCodeSet(100).errorMessageSet("error1").build())
						.timeout(5)
						.build()).build(),
					exit().build(),
					fail().code(200).message("failure").build(),
					jump("L1").build(),
					savepoint().build(),
					verify(expr("false")).code(100).message("failure").build(),
					process("SIMPLE_SCENARIO_1", 0).build()
				).build()).build();

	}
}
