package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.jump;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.service.ContextService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class JumpProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(JumpProcess1Tests.class);
		LinkedLogics.registerProcess(JumpProcess1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v3");
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(jump("L2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").label("L1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").label("L2").build())
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(4);
		assertThat(ctx.getParams().get("list")).asList().contains("v11", "v12", "v32", "v33");
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v12").build(),
						jump("L1").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v13").build()).build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v21").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v22").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v23").build()).build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v31").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v32").label("L1").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v33").build()).build())
				.build();
	}
	
	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2");
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").label("L1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(jump("L1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.build();
	}
	
	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_4", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
	}

	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(jump("L1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.build();
	}

	@Logic(id = "INSERT", version = 1)
	public static void insert(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
	}

	@Logic(id = "INSERT", version = 0)
	public static void insertv2(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value.toUpperCase());
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