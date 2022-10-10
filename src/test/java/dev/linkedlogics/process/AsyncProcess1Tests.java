package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
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
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class AsyncProcess1Tests {
	
	private static ContextService contextService;
	
	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(AsyncProcess1Tests.class);
		LinkedLogics.registerProcess(AsyncProcess1Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}
	
	@Test
	public void testScenario1() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED, 1000)).isTrue();
		long finish = System.currentTimeMillis();

		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(1000);
		contextService.get(contextId).ifPresent(ctx -> {
			assertThat(ctx.getParams().containsKey("list")).isTrue();
			assertThat(ctx.getParams().get("list")).asList().hasSize(3);
			assertThat(ctx.getParams().get("list")).asList().contains(2, 4, 6);
		});
	}
	
	
	@ProcessChain
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("MULTIPLY_ASYNC").input("val1", 3).input("val2", 2).input("delay", 500L).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}
	
	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED, 10000)).isTrue();

		contextService.get(contextId).ifPresent(ctx -> {
			assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
			assertThat(ctx.getError()).isNotNull();
			assertThat(ctx.getParams().get("list")).asList().hasSize(1);
			assertThat(ctx.getParams().get("list")).asList().contains(2);
		});
	}
	
	
	@ProcessChain
	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("MULTIPLY_ASYNC").input("val1", 3).input("val2", 2).input("delay", 6000L).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}
	
	@Logic(id = "MULTIPLY", returnAs = "multiply_result")
	public static Integer multiply(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return value1 * value2;
	}
	
	@Logic(id = "MULTIPLY_ASYNC", returnAsync = true, returnAs = "multiply_result")
	public static void multiply(@Input("val1") Integer value1, @Input("val2") Integer value2, @Input("delay") Long delay) {
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
	public static boolean insert(@Input("list") List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		return false;
	}
	
	@Logic(id = "REMOVE", returnAs = "remove_result")
	public static boolean remove(@Input("list") List<Integer> list, @Input("val") Integer value) {
		return list.remove(value);
	}
	
}
