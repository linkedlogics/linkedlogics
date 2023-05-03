package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
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
import io.linkedlogics.context.ContextBuilder;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.service.ContextService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class CompensateProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(CompensateProcess1Tests.class);
		LinkedLogics.registerProcess(CompensateProcess1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains("v1", "v2", "v3");
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build())
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_2").params("list", new ArrayList<>()).build());
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v4");
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
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build())
				.add(verify(expr("list.size() < 1")).build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced().build())
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v4");
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
				.add(verify(expr("list.size() < 1")).build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced().build())
				.build();
	}

	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_4").params("list", new ArrayList<>()).build());
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v4");
	}

	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(verify(expr("list.size() < 1")).build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v4").forced().build())
				.build();
	}

	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_5").params("list", new ArrayList<>()).build());
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains("v1");
	}

	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").disabled().build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v3")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v3").build())
						.build())
				.add(verify(expr("list.size() < 1")).build())
				.build();
	}

	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_6").params("list", new ArrayList<>()).build());
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains("v2", "v4");
	}

	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v1")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v1").build())
						.build())
				.add(logic("INSERT")
						.input("list", expr("list")).input("val", "v2")
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", "v2").disabled().build())
						.build())
				.add(verify(expr("list.size() < 1")).build())
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
