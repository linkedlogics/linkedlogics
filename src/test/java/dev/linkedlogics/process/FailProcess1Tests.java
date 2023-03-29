package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.branch;
import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.exit;
import static dev.linkedlogics.LinkedLogicsBuilder.fail;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.verify;
import static dev.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class FailProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(FailProcess1Tests.class);
		LinkedLogics.registerProcess(FailProcess1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2");
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(branch(expr("list.size() < 2"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3").build(), 
						fail().code(-5).build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v5").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6").build())
				.build();
	}
	
	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2");
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())
						.build())
				.add(fail().build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced().build())
				.build();
	}
	
	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(5);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v11", "v12", "v13");
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build()).build(),
						logic("INSERT").input("list", expr("list")).input("val", "v12").forced().build(),
						logic("INSERT").input("list", expr("list")).input("val", "v13").forced().build()).build())
				.add(fail().build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced().build())
				.build();
	}

	@Logic(id = "INSERT")
	public static void insert(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
	}

	@Logic(id = "REMOVE")
	public static void remove(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.remove(value);
	}

	@Logic(id = "PRINT")
	public static void print(@Input("list") List<String> list) {
		System.out.println("List = " + list);
	}
}
