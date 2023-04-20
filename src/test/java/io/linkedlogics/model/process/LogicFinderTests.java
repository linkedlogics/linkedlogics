package io.linkedlogics.model.process;

import static io.linkedlogics.LinkedLogicsBuilder.branch;
import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.error;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.group;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.process;
import static io.linkedlogics.LinkedLogicsBuilder.verify;
import static io.linkedlogics.LinkedLogicsBuilder.when;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.model.process.VerifyLogicDefinition;
import io.linkedlogics.model.process.helper.LogicFinder;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class LogicFinderTests {
	public static final String PROCESS_A = "proces_a";
	public static final String PROCESS_B = "proces_b";
	
	public static final String LOGIC_1 = "logic_1";
	public static final String LOGIC_2 = "logic_2";
	public static final String LOGIC_3 = "logic_3";
	
	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerProcess(new ProcessA());
	}
	
	@ParameterizedTest
	@MethodSource("provideFinderForProcessA")
	public void shouldParseProcess1(String processId, Class<? extends BaseLogicDefinition> logicDefinitionClass, int expectedLogicFoundCount) {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(processId, 0);
		List<?> list = LogicFinder.findLogics(process.get(), logicDefinitionClass);
		assertThat(list).hasSize(expectedLogicFoundCount);
	}
	
	private static Stream<Arguments> provideFinderForProcessA() {
		return Stream.of(
			Arguments.of(PROCESS_A, BranchLogicDefinition.class, 3),
			Arguments.of(PROCESS_A, VerifyLogicDefinition.class, 2),
			Arguments.of(PROCESS_A, GroupLogicDefinition.class, 3),
			Arguments.of(PROCESS_B, ProcessLogicDefinition.class, 10)
		);
	}
	
	public static class ProcessA {
		public ProcessDefinition createProcessA0() {
			return createProcess(PROCESS_A, 0)
					.add(logic(LOGIC_1)
							.compensate(logic(LOGIC_2).build())
							.build())
					.add(group(logic(LOGIC_1).compensate(logic(LOGIC_2).build()).build(),
							group(logic(LOGIC_1).compensate(logic(LOGIC_2).build()).build(),
									logic(LOGIC_1).compensate(logic(LOGIC_2).build()).build(),
									verify(expr("false")).code(-200).message("failure").build())
								.handle(error().withCodes(-200).orMessages("error").throwAgain(-500, "another error").build())
								.build(),
							logic(LOGIC_1).compensate(logic(LOGIC_2).build()).build())
						.handle(error().withCodes(-500).orMessages("error").build())
						.build())
					.add(branch(expr("list.size() < 2"), 
							logic(LOGIC_1).build(), 
							branch(expr("list.size() < 2"), 
									logic(LOGIC_1).build(), 
									group(logic(LOGIC_1).build()).build()).build()).build())
					.add(logic(LOGIC_1).build())
					.add(verify(when("false")).elseFailWithCode(-200).andMessage("failure").handle(error().usingLogic(branch(expr("list.size() < 2"),logic(LOGIC_1).build()).build()).build()).build())
					.build();
		}
		
		public ProcessDefinition createProcessB0() {
			return createProcess(PROCESS_B, 0)
					.add(process(PROCESS_A, 0).build())
					.add(group(process(PROCESS_A, 0).build(),
							group(process(PROCESS_A, 0).build(),
									process(PROCESS_A, 0).build())
								.build(),
							process(PROCESS_A, 0).build())
						.build())
					.add(branch(expr("list.size() < 2"), 
							process(PROCESS_A, 0).build(), 
							branch(expr("list.size() < 2"), 
									process(PROCESS_A, 0).build(), 
									group(process(PROCESS_A, 0).build()).build()).build()).build())
					.add(process(PROCESS_A, 0).build())
					.add(verify(expr("false")).elseFailWithCode(-200).andMessage("failure").handle(error().usingLogic(process(PROCESS_A, 0).build()).build()).build())
					.build();
		}
	}
}

