package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.verify;
import static dev.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.LinkedLogicsCallback;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class CallBack1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(CallBack1Tests.class);
		LinkedLogics.registerProcess(CallBack1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
	}

	@Test
	public void testScenario1() {
		AtomicBoolean result = new AtomicBoolean();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("s", "hello");}},
				new LinkedLogicsCallback() {
					
					@Override
					public void onTimeout() {
						
					}
					
					@Override
					public void onSuccess(Context context) {
						String s = (String) context.getParams().get("s");
						result.set(s.equals("HELLO"));						
					}
					
					@Override
					public void onFailure(Context context, ContextError error) {
						
					}
				});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		assertThat(result.get()).isTrue();
	}

	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("STRING_UPPER").input("s", expr("s")).returnAs("s").build())
				.build();
	}
	
	@Test
	public void testScenario2() {
		AtomicBoolean result = new AtomicBoolean();
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("s", "hello");}},
				new LinkedLogicsCallback() {
					
					@Override
					public void onTimeout() {
						
					}
					
					@Override
					public void onSuccess(Context context) {
					
					}
					
					@Override
					public void onFailure(Context context, ContextError error) {
						result.set(true);
					}
				});
		assertThat(waitUntil(contextId, Status.FAILED)).isTrue();
		assertThat(result.get()).isTrue();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("STRING_UPPER").input("s", expr("s")).returnAs("s").build())
				.add(verify(expr("false")).build())
				.build();
	}

	@Logic(id = "STRING_UPPER")
	public static String upper(@Input("s") String s) {
		return s.toUpperCase();
	}
	
	@Logic(id = "STRING_LOWER")
	public static String lower(@Input("s") String s) {
		return s.toLowerCase();
	}
}
