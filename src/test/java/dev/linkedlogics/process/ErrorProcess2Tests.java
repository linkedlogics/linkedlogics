package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.verify;
import static dev.linkedlogics.LinkedLogicsBuilder.error;
import static dev.linkedlogics.process.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.annotation.ProcessChain;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError.ErrorType;
import dev.linkedlogics.exception.LogicException;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class ErrorProcess2Tests {

	private static ContextService contextService;

	private static AtomicInteger retryCounter;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(ErrorProcess2Tests.class);
		LinkedLogics.registerProcess(ErrorProcess2Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@BeforeEach
	public void resetCounter() {
		retryCounter = new AtomicInteger();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
	}


	@ProcessChain
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(3);
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
	}

	@ProcessChain
	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).retry(3, 0).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().containsKey("list")).isTrue();

		assertThat(retryCounter.get()).isEqualTo(2);
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(1, 2);
	}

	@ProcessChain
	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT_SAFE").input("list", expr("list")).input("val", 1).retry(3, 0).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_4", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(3);
		assertThat(ctx.getParams().get("list")).asList().hasSize(3);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
	}

	@ProcessChain
	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(group(logic("INSERT").input("list", expr("list")).input("val", 2).build(),
						logic("INSERT").input("list", expr("list")).input("val", 1).build())
						.retry(3, 0)
						.build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 3).build())
				.build();
	}

	@Test
	public void testScenario5() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_5", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
	}

	@ProcessChain
	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 3).handle(error().build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}

	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_6", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
	}

	@ProcessChain
	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 3).handle(error(-100).build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}


	@Test
	public void testScenario7() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_7", new HashMap<>() {{ put("list", new ArrayList<>());}});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
	}

	@ProcessChain
	public static ProcessDefinition scenario7() {
		return createProcess("SIMPLE_SCENARIO_7", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 3).handle(error(-100).build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).forced().build())
				.build();
	}

	@Logic(id = "INSERT", returnAs = "insert_result")
	public static boolean insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		retryCounter.incrementAndGet();
		throw new LogicException(-1, "value is not even", ErrorType.TEMPORARY);
	}

	@Logic(id = "INSERT_SAFE", returnAs = "insert_result")
	public static boolean insertSafe(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		if (value % 2 == 0) {
			list.add(value);
			return true;
		}
		retryCounter.incrementAndGet();
		if (retryCounter.get() == 2) {
			list.add(value % 2);
			return true;
		} else {
			throw new LogicException(-1, "value is not even", ErrorType.TEMPORARY);
		}
	}

	@Logic(id = "REMOVE", returnAs = "remove_result")
	public static boolean remove(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		return list.remove(value);
	}
}
