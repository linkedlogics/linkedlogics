package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextBuilder;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.service.common.QueueCallbackService;
import io.linkedlogics.service.common.QueueConsumerService;
import io.linkedlogics.service.common.QueuePublisherService;
import io.linkedlogics.service.common.QueueSchedulerService;
import io.linkedlogics.service.local.LocalServiceConfigurer;
import io.linkedlogics.test.TestContextService;

public class CallBack2Tests {

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer()
				.configure(new TestContextService())
				.configure(new QueueCallbackService())
				.configure(new QueueConsumerService())
				.configure(new QueuePublisherService())
				.configure(new QueueSchedulerService()));
		LinkedLogics.registerLogic(CallBack2Tests.class);
		LinkedLogics.registerProcess(CallBack2Tests.class);
		LinkedLogics.launch();
	}
	
	@AfterAll
	public static void stop() {
		LinkedLogics.shutdown();
	}

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
