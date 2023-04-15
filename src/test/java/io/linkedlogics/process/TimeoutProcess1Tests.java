package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
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

public class TimeoutProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(TimeoutProcess1Tests.class);
		LinkedLogics.registerProcess(TimeoutProcess1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED, 2000)).isTrue();
		long finish = System.currentTimeMillis();
		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(1500);
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("MULTIPLY_ASYNC").input("val1", 3).input("val2", 2).input("delay", 2000L).timeout(1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}
	
	@Test
	public void testScenario2() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED, 2000)).isTrue();
		long finish = System.currentTimeMillis();
		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(1500);
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
	}


	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("MULTIPLY_LONG").input("val1", 3).input("val2", 2).input("delay", 2000L).timeout(1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}

	@Test
	public void testScenario3() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED, 2000)).isTrue();
		long finish = System.currentTimeMillis();
		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(1500);
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
	}


	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("MULTIPLY_LONG").input("val1", 3).input("val2", 2).input("delay", 1000L).timeout(5).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}
	
	@Test
	public void testScenario4() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_4", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED, 2000)).isTrue();
		long finish = System.currentTimeMillis();
		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(1500);
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 6, 8);
	}


	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(group(logic("MULTIPLY_LONG").input("val1", 3).input("val2", 2).input("delay", 2000).build(),
						logic("INSERT").input("list", expr("list")).input("val", 4).build(),
						logic("INSERT").input("list", expr("list")).input("val", 6).forced().build()).timeout(1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 8).forced().build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 10).build())
				.build();
	}
	
	@Test
	public void testScenario5() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_5", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED, 2000)).isTrue();
		long finish = System.currentTimeMillis();
		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(1500);
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(6, 8);
	}


	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2)
						.compensate(logic("REMOVE").input("list", expr("list")).input("val", 2).build()).build())
				.add(group(logic("MULTIPLY_LONG").input("val1", 3).input("val2", 2).input("delay", 2000).build(),
						logic("INSERT").input("list", expr("list")).input("val", 4).build(),
						logic("INSERT").input("list", expr("list")).input("val", 6).forced().build()).timeout(1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 8).forced().build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 10).build())
				.build();
	}
	
	@Logic(id = "MULTIPLY_LONG", returnAs = "multiply_result")
	public static Integer multiply(@Input("val1") Integer value1, @Input("val2") Integer value2, @Input("delay") Long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {}
		return value1 * value2;
	}

	@Logic(id = "MULTIPLY_ASYNC", returnAsync = true, returnAs = "multiply_result")
	public static void multiplyAsync(@Input("val1") Integer value1, @Input("val2") Integer value2, @Input("delay") Long delay) {
		String contextId = LinkedLogics.getContextId();
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);
				} catch (InterruptedException e) {}
				LinkedLogics.asyncCallback(contextId, value1 * value2);
			}
		}).start();
	}

	@Logic(id = "INSERT", returnAs = "insert_result")
	public static boolean insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		return false;
	}
	
	@Logic(id = "REMOVE", returnAs = "remove_result")
	public static boolean remove(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		return list.remove(value);
	}
}
