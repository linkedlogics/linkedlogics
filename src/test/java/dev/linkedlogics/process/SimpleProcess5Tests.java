package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.process;
import static dev.linkedlogics.process.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.annotation.ProcessChain;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class SimpleProcess5Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(SimpleProcess5Tests.class);
		LinkedLogics.registerProcess(SimpleProcess5Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(9);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3", "v4", "v5", "v6");
	}
	
	@ProcessChain
	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(process("SIMPLE_SCENARIO_2", 0).build())
				.add(process("SIMPLE_SCENARIO_1", 0).build())
				.build();
	}
	
	@ProcessChain
	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.add(process("SIMPLE_SCENARIO_1", 0).build())
				.build();
	}

	
	@ProcessChain
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v5").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6").build())
				.build();
	}
	

	@Logic(id = "INSERT", returnAs = "list")
	public static void insertv2(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
	}

	@Logic(id = "REMOVE", returnAs = "list")
	public static List<String> remove(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.remove(value);
		return list;
	}

	@Logic(id = "PRINT")
	public static void print(@Input("list") List<String> list) {
		System.out.println("List = " + list);
	}

}
