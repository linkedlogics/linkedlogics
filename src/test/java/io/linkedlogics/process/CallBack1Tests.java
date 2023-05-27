package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextBuilder;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.test.LinkedLogicsExtension;
import io.linkedlogics.test.TestContextService;

@ExtendWith(LinkedLogicsExtension.class)
public class CallBack1Tests {

	@Test
	public void testScenario1() {
		AtomicBoolean result = new AtomicBoolean();
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_1").params("s", "hello").build(),
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
		TestContextService.blockUntil();
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FINISHED);
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
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_2").params("s", "hello").build(),
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
		TestContextService.blockUntil();
		Context ctx = TestContextService.getCurrentContext();
		assertThat(ctx.getId()).isEqualTo(contextId);
		assertThat(ctx.getStatus()).isEqualTo(Status.FAILED);
		assertThat(result.get()).isTrue();
	}

	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("STRING_UPPER").input("s", expr("s")).returnAs("s").build())
				.add(verify(expr("false")).build())
				.build();
	}
	
	@Test
	public void testScenario3() {
		AtomicBoolean result = new AtomicBoolean();
		String contextId = LinkedLogics.start(ContextBuilder.process("SIMPLE_SCENARIO_3").params("s", "hello").build(),
				new LinkedLogicsCallback() {
					
					@Override
					public void onTimeout() {
						
					}
					
					@Override
					public void onSuccess(Context context) {

					}
					
					@Override
					public void onCancellation(Context context) {
						result.set(true);
					}
				});
		try {
			LinkedLogics.cancel(contextId);
			Thread.sleep(5000);
		} catch (InterruptedException e) { 	}
		assertThat(result.get()).isTrue();
	}

	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("STRING_UPPER_SLOW").input("s", expr("s")).returnAs("s").timeout(3).build())
				.add(logic("STRING_UPPER").input("s", "s1").returnAs("s1").build())
				.add(logic("STRING_UPPER").input("s", "s2").returnAs("s2").build())
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
	
	@Logic(id = "STRING_UPPER_SLOW")
	public static String upperSlow(@Input("s") String s) {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) { }
		return s.toUpperCase();
	}
}
