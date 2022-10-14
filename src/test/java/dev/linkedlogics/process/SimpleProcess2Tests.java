package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.branch;
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

public class SimpleProcess2Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(SimpleProcess2Tests.class);
		LinkedLogics.registerProcess(SimpleProcess2Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v4");
	}


	@ProcessChain
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(branch(expr("list.size() < 2"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3").build(), 
						logic("INSERT").input("list", expr("list")).input("val", "v4").build()).build())
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2");
	}


	@ProcessChain
	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(branch(expr("list.size() < 2"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3").build()).build())
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
	}


	@ProcessChain
	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(branch(expr("list.size() > 0"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3").build(), 
						logic("INSERT").input("list", expr("list")).input("val", "v4").build()).build())
				.build();
	}

	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_4", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
	}


	@ProcessChain
	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(branch(expr("list.size() > 0"), 
						logic("INSERT").input("list", expr("list")).input("val", "v3").build()).build())
				.build();
	}

	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_5", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v4");
	}


	@ProcessChain
	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(branch(expr("list.size() > 0"), 
						branch(expr("list.size() > 2"), 
								logic("INSERT").input("list", expr("list")).input("val", "v3").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v4").build()).build(),
						logic("INSERT").input("list", expr("list")).input("val", "v5").build()).build())
				.build();
	}

	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_6", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(6);
		assertThat(ctx.getParams().get("list")).asList().contains("v11", "v12", "v13", "v31", "v32", "v33");
	}


	@ProcessChain
	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v12").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v13").build()).build())
				.add(branch(expr("list.size() > 5"), 
						group(logic("INSERT").input("list", expr("list")).input("val", "v21").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v22").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v23").build()).build(),
						group(logic("INSERT").input("list", expr("list")).input("val", "v31").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v32").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v33").build()).build()).build())
				.build();
	}

	@Test
	public void testScenario7() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_7", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v11", "v12", "v13");
	}


	@ProcessChain
	public static ProcessDefinition scenario7() {
		return createProcess("SIMPLE_SCENARIO_7", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v11").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v12").build(),
						logic("INSERT").input("list", expr("list")).input("val", "v13").build()).build())
				.add(branch(expr("list.size() < 5"), 
						group(logic("INSERT").input("list", expr("list")).input("val", "v21").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v22").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v23").build()).disabled().build(),
						group(logic("INSERT").input("list", expr("list")).input("val", "v31").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v32").build(),
								logic("INSERT").input("list", expr("list")).input("val", "v33").build()).build()).build())
				.build();
	}

	@Test
	public void testScenario8() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_8", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v2");
	}


	@ProcessChain
	public static ProcessDefinition scenario8() {
		return createProcess("SIMPLE_SCENARIO_8", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(branch(expr("list.size() > 0"), 
						logic("REMOVE").input("list", expr("list")).input("val", "v1").build()).build())
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
