package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.branch;
import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.process;
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

public class SimpleProcess4Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(SimpleProcess4Tests.class);
		LinkedLogics.registerProcess(SimpleProcess4Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ 
			put("list", new ArrayList<>());
			put("val", 3);
		}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(6);
	}

	@Test
	public void testScenario2() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ 
			put("list", new ArrayList<>());
			put("val", 7);
		}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();

		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("list")).isTrue();
		assertThat(ctx.getParams().get("list")).asList().hasSize(1);
		assertThat(ctx.getParams().get("list")).asList().contains(28);
	}
	

	public static ProcessDefinition scenario2v0() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("MULTIPLY").input("val1", expr("val")).input("val2", 2).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")).build())
				.build();
	}


	public static ProcessDefinition scenario2v1() {
		return createProcess("SIMPLE_SCENARIO_2", 1)
				.add(logic("MULTIPLY").input("val1", expr("val")).input("val2", 4).build())
				.add(logic("INSERT").input("list", expr("list")).input("val", expr("multiply_result")).build())
				.build();
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(branch(expr("val > 5"), 
						process("SIMPLE_SCENARIO_2", 1).build(),
						process("SIMPLE_SCENARIO_2", 0).build())
					.build())
				.build();
	}

	@Logic(id = "MULTIPLY", returnAs = "multiply_result")
	public static Integer multiply(@Input("val1") Integer value1, @Input("val2") Integer value2) {
		return value1 * value2;
	}

	@Logic(id = "INSERT", returnAs = "insert_result")
	public static boolean insert(@Input(value = "list", returned = true) List<Integer> list, @Input("val") Integer value) {
		list.add(value);
		return true;
	}
}
