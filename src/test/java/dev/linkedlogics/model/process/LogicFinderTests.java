package dev.linkedlogics.model.process;

import static dev.linkedlogics.LinkedLogicsBuilder.branch;
import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.error;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.group;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.LinkedLogicsBuilder.process;
import static dev.linkedlogics.LinkedLogicsBuilder.verify;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.process.helper.LogicFinder;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

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
			Arguments.of(PROCESS_A, ErrorLogicDefinition.class, 2),
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
								.handle(error(-200).errorMessageSet(Set.of("error")).throwAgain(-500, "another error").build())
								.build(),
							logic(LOGIC_1).compensate(logic(LOGIC_2).build()).build())
						.handle(error(-500).errorMessageSet(Set.of("error")).build())
						.build())
					.add(branch(expr("list.size() < 2"), 
							logic(LOGIC_1).build(), 
							branch(expr("list.size() < 2"), 
									logic(LOGIC_1).build(), 
									group(logic(LOGIC_1).build()).build()).build()).build())
					.add(logic(LOGIC_1).build())
					.add(error().build())
					.add(verify(expr("false")).code(-200).message("failure").build())
					.add(error().errorLogic(branch(expr("list.size() < 2"),logic(LOGIC_1).build()).build()).build())
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
					.add(error().build())
					.add(verify(expr("false")).code(-200).message("failure").build())
					.add(error().errorLogic(process(PROCESS_A, 0).build()).build())
					.build();
		}
	}
}


