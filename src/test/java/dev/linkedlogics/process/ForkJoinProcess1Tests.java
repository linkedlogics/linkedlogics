package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class ForkJoinProcess1Tests {
	
	private static ContextService contextService;
	
	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(ForkJoinProcess1Tests.class);
		LinkedLogics.registerProcess(ForkJoinProcess1Tests.class);
		contextService = ServiceLocator.getInstance().getContextService();
	}
	
	@Test
	public void testScenario1() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>());
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		long finish = System.currentTimeMillis();

		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(2500);
		contextService.get(contextId).ifPresent(ctx -> {
			assertThat(ctx.getParams().containsKey("concat")).isTrue();
			assertThat(ctx.getParams().get("concat")).asString().contains("v1");
			assertThat(ctx.getParams().get("concat")).asString().contains("v2");
			assertThat(ctx.getParams().get("concat")).asString().contains("v3");
		});
	}
	
	

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("CREATE")
						.input("key", "key1").input("value", "v1").input("delay", 1000L).fork("F1")
						.build())
				.add(logic("CREATE")
						.input("key", "key2").input("value", "v2").input("delay", 1500L).fork("F2")
						.build())
				.add(logic("CREATE")
						.input("key", "key3").input("value", "v3").input("delay", 2000L).fork("F3")
						.build())
				.add(logic("CONCAT")
						.input("val1", expr("key1")).input("val2", expr("key2")).input("val3", expr("key3")).join("F1", "F2", "F3")
						.build())
				.build();
	}
	
	@Test
	public void testScenario2() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>());
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		long finish = System.currentTimeMillis();

		assertThat(finish - start).isGreaterThan(500);
		assertThat(finish - start).isLessThan(2500);
		contextService.get(contextId).ifPresent(ctx -> {
			assertThat(ctx.getParams().containsKey("concat")).isTrue();
			assertThat(ctx.getParams().get("concat")).asString().contains("v1");
			assertThat(ctx.getParams().get("concat")).asString().contains("v3");
			assertThat(ctx.getParams().get("concat_2")).asString().contains("v21");
			assertThat(ctx.getParams().get("concat_2")).asString().contains("v22");
			assertThat(ctx.getParams().get("concat_2")).asString().contains("v23");
		});
	}
	
	

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("CREATE").input("key", "key1").input("value", "v1").input("delay", 1000L).fork("F1").build())
				.add(group(logic("CREATE").input("key", "key21").input("value", "v21").input("delay", 750L).build(),
						   logic("CREATE").input("key", "key22").input("value", "v22").input("delay", 750L).build(),
						   logic("CREATE").input("key", "key23").input("value", "v23").input("delay", 500L).build())
						.fork("F2")
						.build())
				.add(logic("CREATE")
						.input("key", "key3").input("value", "v3").input("delay", 1500L).fork("F3")
						.build())
				.add(logic("CONCAT")
						.input("val1", expr("key1")).input("val2", "null").input("val3", expr("key3")).join("F1", "F3")
						.build())
				.add(logic("CONCAT")
						.input("val1", expr("key21")).input("val2", expr("key22")).input("val3", expr("key23")).returnAs("concat_2").join("F2")
						.build())
				.build();
	}
	
	@Test
	public void testScenario3() {
		long start = System.currentTimeMillis();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>());
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		long finish = System.currentTimeMillis();

		assertThat(finish - start).isLessThan(50);
		contextService.get(contextId).ifPresent(ctx -> {
			assertThat(ctx.getParams().containsKey("concat")).isTrue();
			assertThat(ctx.getParams().get("concat")).asString().contains("key1");
			assertThat(ctx.getParams().get("concat")).asString().contains("key2");
			assertThat(ctx.getParams().get("concat")).asString().contains("key3");
		});
	}
	
	

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("CREATE")
						.input("key", "key1").input("value", "v1").input("delay", 1000L).fork()
						.build())
				.add(logic("CREATE")
						.input("key", "key2").input("value", "v2").input("delay", 1500L).fork()
						.build())
				.add(logic("CREATE")
						.input("key", "key3").input("value", "v3").input("delay", 2000L).fork()
						.build())
				.add(logic("CONCAT")
						.input("val1", "key1").input("val2", "key2").input("val3", "key3")
						.build())
				.build();
	}
	
	@Logic(id = "CREATE", returnMap = true)
	public static Map<String, String> create(@Input("key") String key, @Input("value") String value, @Input("delay") long delay) {
		try {
			Thread.sleep(delay);
		} catch (InterruptedException e) {}
		return Map.of(key, value);
	}
	
	@Logic(id = "CONCAT", returnAs = "concat")
	public static String concat(@Input("val1") String val1, @Input("val2") String val2, @Input("val3") String val3) {
		return val1 + ":" + val2 + ":" + val3;
	}
}
