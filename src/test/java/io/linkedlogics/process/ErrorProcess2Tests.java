package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
import static io.linkedlogics.test.LinkedLogicsTest.assertContext;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextBuilder;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.context.Status;
import io.linkedlogics.exception.LogicException;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.TestContextService;

@ExtendWith(LinkedLogicsExtension.class)
public class ErrorProcess2Tests {

	private static AtomicInteger retryCounter;

	@BeforeEach
	public void resetCounter() {
		retryCounter = new AtomicInteger();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_1").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").isNotExecuted();
	}



	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_2").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(4);
		assertThat(ctx.getParams().get("list")).asList().hasSize(0);
		assertThat(ctx.getParams().get("list")).asList().contains();
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isRetried(3);
		assertContext().when("2").isNotExecuted();
	}


	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 1).retry(3, 0).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario3() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_3").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().containsKey("list")).isTrue();

		assertThat(retryCounter.get()).isEqualTo(2);
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(1, 2);
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isRetried(1);
		assertContext().when("2").isExecuted();
	}


	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("INSERT_SAFE").input("list", expr("list")).input("val", 1).retry(3, 0).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.build();
	}

	@Test
	public void testScenario4() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_4").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(retryCounter.get()).isEqualTo(4);
		assertThat(ctx.getParams().get("list")).asList().hasSize(4);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
		
		assertContext().when("1").isExecuted();
		assertContext().when("1").onError().isRetried(3);
		assertContext().when("2").isNotExecuted();
	}


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
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_5").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").onError().isHandled();
		assertContext().when("3").isExecuted();
	}


	public static ProcessDefinition scenario5() {
		return createProcess("SIMPLE_SCENARIO_5", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 3).handle(error().build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}

	@Test
	public void testScenario6() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_6").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(2);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").onError().isNotHandled();
		assertContext().when("3").isNotExecuted();
	}


	public static ProcessDefinition scenario6() {
		return createProcess("SIMPLE_SCENARIO_6", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 3).handle(error().withCodes(-100).build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
				.build();
	}


	@Test
	public void testScenario7() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_7").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 4);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").onError().isNotHandled();
		assertContext().when("3").isExecuted();
	}


	public static ProcessDefinition scenario7() {
		return createProcess("SIMPLE_SCENARIO_7", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 3).handle(error().withCodes(-100).build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).forced().build())
				.build();
	}
	
	@Test
	public void testScenario8() {
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_8").params("list", new ArrayList<>()).build());
		TestContextService.blockUntil();
		
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(2);
		assertThat(ctx.getParams().get("list")).asList().contains(2, 6);
		
		assertContext().when("1").isExecuted();
		assertContext().when("2").onError().isHandled();
		assertContext().when("3").isNotExecuted();
	}


	public static ProcessDefinition scenario8() {
		return createProcess("SIMPLE_SCENARIO_8", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", 2).build())
				.add(verify(expr("false")).handle(error().usingLogic(logic("INSERT").input("list", expr("list")).input("val", 6).build()).throwAgain().build()).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", 4).build())
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
