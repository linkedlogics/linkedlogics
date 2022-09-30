package dev.linkedlogics.model.process;

import static dev.linkedlogics.model.process.ProcessDefinition.createProcess;
import static dev.linkedlogics.model.process.SingleLogicDefinition.logic;
import static dev.linkedlogics.model.process.GroupLogicDefinition.group;
import static dev.linkedlogics.model.process.BranchLogicDefinition.branch;
import static dev.linkedlogics.model.process.ErrorLogicDefinition.error;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.ProcessChain;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class ProcessTests {
	public static final String PROCESS_A = "proces_a";
	
	public static final String LOGIC_1 = "logic_1";
	public static final String LOGIC_2 = "logic_2";
	public static final String LOGIC_3 = "logic_3";
	
	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerProcess(new ProcessA());
	}
	
	@Test
	public void shouldParseProcess1() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 0);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(process.get().getLogicByPosition("2").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("3").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("3").get()).getLogicId()).isEqualTo(LOGIC_3);
		assertThat(process.get().getLogicByPosition("4")).isEmpty();
	}
	
	@Test
	public void shouldParseProcess2() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 1);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.2").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("2").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.1").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.2").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	@Test
	public void shouldParseProcess3() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 2);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1.2").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("2.1").get()).isInstanceOf(BranchLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.1a").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.1b").get()).getLogicId()).isEqualTo(LOGIC_3);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2.2").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	@Test
	public void shouldParseProcess4() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 3);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1").get()).getLogicId()).isEqualTo(LOGIC_1);
		assertThat(process.get().getLogicByPosition("1R").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1R").get()).getLogicId()).isEqualTo(LOGIC_2);
		assertThat(process.get().getLogicByPosition("2").get()).isInstanceOf(SingleLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("2").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	@Test
	public void shouldParseProcess5() {
		Optional<ProcessDefinition> process = ServiceLocator.getInstance().getProcessService().getProcess(PROCESS_A, 4);
		assertThat(process.get().getLogicByPosition("1").get()).isInstanceOf(GroupLogicDefinition.class);
		assertThat(((SingleLogicDefinition)process.get().getLogicByPosition("1E").get()).getLogicId()).isEqualTo(LOGIC_3);
	}
	
	public static class ProcessA {
		@ProcessChain
		public ProcessDefinition createProcessA0() {
			return createProcess(PROCESS_A, 0)
					.add(logic(LOGIC_1).build())
					.add(logic(LOGIC_2).build())
					.add(logic(LOGIC_3).build())
					.build();
		}
		
		@ProcessChain
		public ProcessDefinition createProcessA1() {
			return createProcess(PROCESS_A, 1)
					.add(group(
								logic(LOGIC_1).build(), 
								logic(LOGIC_2).build())
						.build())
					.add(group(
								logic(LOGIC_2).build(), 
								logic(LOGIC_3).build())
						.build())
					.build();
		}
		
		@ProcessChain
		public ProcessDefinition createProcessA2() {
			return createProcess(PROCESS_A, 2)
					.add(group(
								logic(LOGIC_1).build(), 
								logic(LOGIC_2).build())
						.build())
					.add(group(
								branch(true, logic(LOGIC_2).build(), logic(LOGIC_3).build()).build(), 
								logic(LOGIC_3).build())
						.build())
					.build();
		}
		
		@ProcessChain
		public ProcessDefinition createProcessA3() {
			return createProcess(PROCESS_A, 3)
					.add(logic(LOGIC_1).compensate(logic(LOGIC_2).build()).build())
					.add(logic(LOGIC_3).build())
					.build();
		}
		
		@ProcessChain
		public ProcessDefinition createProcessA4() {
			return createProcess(PROCESS_A, 4)
					.add(group(
								logic(LOGIC_1).build(), 
								logic(LOGIC_2).build())
						.handle(error(-1).errorLogic(logic(LOGIC_3).build()).build())
						.build())
					.add(group(
								logic(LOGIC_2).build(), 
								logic(LOGIC_3).build())
						.build())
					.build();
		}
	}
}
