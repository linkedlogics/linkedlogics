package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.error;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.savepoint;
import static dev.linkedlogics.LinkedLogicsBuilder.verify;
import static dev.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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

public class ErrorProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(ErrorProcess1Tests.class);
		LinkedLogics.registerProcess(ErrorProcess1Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v4");
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").forced().build())
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.build();
	}

	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_4", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v4");
	}

	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.handle(error(-100).build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.build();
	}

	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_5", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
	}

	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.handle(error(-200).build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.build();
	}

	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_6", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v4");
	}

	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.handle(error(-200).errorMessageSet("fail").build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.build();
	}

	@Test
	public void testScenario7() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_7", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
	}

	public static ProcessDefinition scenario7() {
		return createProcess("SIMPLE_SCENARIO_7", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.handle(error(-200).errorMessageSet("error").build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.build();
	}

	@Test
	public void testScenario8() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_8", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v6");
	}

	public static ProcessDefinition scenario8() {
		return createProcess("SIMPLE_SCENARIO_8", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						group(logic("INSERT").input("list", expr("list")).input("val", "v3")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())	
								.build(),
								logic("INSERT").input("list", expr("list")).input("val", "v4")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v4").build())
								.build(),
								verify(expr("false")).code(-200).message("failure").build())
						.handle(error(-100).errorMessageSet("error").build())
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v5")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v5").build())
						.build()
						)
						.handle(error(-200).errorMessageSet("error").build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6").build())
				.build();
	}

	@Test
	public void testScenario9() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_9", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v6", "v7");
	}

	public static ProcessDefinition scenario9() {
		return createProcess("SIMPLE_SCENARIO_9", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						group(logic("INSERT").input("list", expr("list")).input("val", "v3")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())	
								.build(),
								logic("INSERT").input("list", expr("list")).input("val", "v4")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v4").build())
								.build(),
								verify(expr("false")).code(-200).message("failure").build())
						.handle(error(-100).errorMessageSet("error").build())
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v5")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v5").build())
						.build()
						)
						.handle(error(-200).errorMessageSet("error").errorLogic(logic("INSERT").input("list", expr("list")).input("val", "v6").build()).build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v7").build())
				.build();
	}

	@Test
	public void testScenario10() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_10", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v7");
	}

	public static ProcessDefinition scenario10() {
		return createProcess("SIMPLE_SCENARIO_10", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						group(logic("INSERT").input("list", expr("list")).input("val", "v3")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())	
								.build(),
								logic("INSERT").input("list", expr("list")).input("val", "v4")
								.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v4").build())
								.build(),
								verify(expr("false")).code(-200).message("failure").build())
						.handle(error(-100).errorMessageSet("error").build())
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v5")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v5").build())
						.build()
						)
						.handle(error(-200).errorMessageSet("error").errorLogic(logic("INSERT").input("list", expr("list")).input("val", "v6").disabled().build()).build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v7").build())
				.build();
	}
	
	@Test
	public void testScenario11() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_11", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
	}

	public static ProcessDefinition scenario11() {
		return createProcess("SIMPLE_SCENARIO_11", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(savepoint().build())
				.add(group(logic("INSERT").input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())	
						.build(),
						logic("INSERT").input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build(),
						verify(expr("false")).code(-100).message("failure").build())
						.handle(error(-200).build())
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
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
}
