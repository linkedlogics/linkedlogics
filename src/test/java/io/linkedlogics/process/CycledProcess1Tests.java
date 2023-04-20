package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.process;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.exception.MissingLogicError;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class CycledProcess1Tests {

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(CycledProcess1Tests.class);
		LinkedLogics.registerProcess(CycledProcess1Tests.class);
		LinkedLogics.launch();
	}
	
	@Test
	public void testScenario1() {
		assertThatThrownBy(() -> {
			LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ 
				put("list", new ArrayList<>());
			}});
		}).isInstanceOf(MissingLogicError.class);
	}
	
	@Test
	public void testScenario2() {
		assertThatThrownBy(() -> {
			LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ 
				put("list", new ArrayList<>());
			}});
		}).isInstanceOf(MissingLogicError.class);
	}
	
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.add(process("SIMPLE_SCENARIO_2", 0).build())
				.build();
	}
	
	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v4").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v5").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v6").build())
				.add(process("SIMPLE_SCENARIO_1", 0).build())
				.build();
	}
	

	@Logic(id = "INSERT", returnAs = "list")
	public static void insertv2(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.add(value);
	}

	@Logic(id = "REMOVE", returnAs = "list")
	public static List<String> remove(@Input(value = "list", returned = true) List<String> list, @Input("val") String value) {
		list.remove(value);
		return list;
	}

	@Logic(id = "PRINT")
	public static void print(@Input("list") List<String> list) {
		System.out.println("List = " + list);
	}
}