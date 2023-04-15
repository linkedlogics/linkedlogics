package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.service.ContextService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.local.LocalServiceConfigurer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class InputProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(InputProcess1Tests.class);
		LinkedLogics.registerProcess(InputProcess1Tests.class);
		LinkedLogics.launch();
		contextService = ServiceLocator.getInstance().getContextService();
		ServiceLocator.getInstance().getMapperService();
	}

	@Test
	public void testScenario1() {
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_1", new HashMap<>() {{ put("person", new Person("firstname", "lastname", "FREE"));}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("person")).isTrue();
		assertThat(((Map<String, Object>) ctx.getParams().get("person")).get("state")).isEqualTo("BUSY");
		assertThat(ctx.getParams().get("fullname")).isEqualTo("firstname lastname");
	}


	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("GET_FULLNAME").input("person", expr("person")).build())
				.add(logic("SET_STATE").input("person", expr("person")).build())
				.build();
	}
	
	@Test
	public void testScenario2() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("firstname", "lastname", "FREE"));
		persons.add(new Person("firstname", "lastname", "BUSY"));
		persons.add(new Person("firstname", "lastname", "FREE"));
		persons.add(new Person("firstname", "lastname", "BUSY"));
		persons.add(new Person("firstname", "lastname", "BUSY"));
		
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_2", new HashMap<>() {{ put("persons", persons);}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("busy_person_count")).isTrue();
		assertThat(ctx.getParams().get("busy_person_count")).isEqualTo(3);
	}


	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(logic("COUNT_BUSY_STATES").input("persons", expr("persons")).build())
				.build();
	}
	
	@Test
	public void testScenario3() {
		List<Person> persons = new ArrayList<>();
		persons.add(new Person("firstname1", "lastname1", "FREE"));
		persons.add(new Person("firstname2", "lastname2", "FREE"));
		persons.add(new Person("firstname3", "lastname3", "FREE"));
		persons.add(new Person("firstname1", "lastname1", "FREE"));
		persons.add(new Person("firstname3", "lastname3", "FREE"));
		
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_3", new HashMap<>() {{ put("persons", persons);}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("unique_person_count")).isTrue();
		assertThat(ctx.getParams().get("unique_person_count")).isEqualTo(3);
	}


	public static ProcessDefinition scenario3() {
		return createProcess("SIMPLE_SCENARIO_3", 0)
				.add(logic("COUNT_UNIQUE").input("persons", expr("persons")).build())
				.build();
	}
	
	@Test
	public void testScenario4() {
		Map<String, Person> persons = new HashMap<>();
		persons.put("firstname1", new Person("firstname1", "lastname1", "FREE"));
		persons.put("firstname2", new Person("firstname2", "lastname2", "FREE"));
		persons.put("firstname3", new Person("firstname3", "lastname3", "FREE"));
		persons.put("firstname4", new Person("firstname4", "lastname4", "FREE"));
		persons.put("firstname5", new Person("firstname5", "lastname5", "FREE"));
		
		String contextId = LinkedLogics.start("SIMPLE_SCENARIO_4", new HashMap<>() {{ put("persons", persons);}});
		assertThat(waitUntil(contextId, Status.FINISHED)).isTrue();
		
		Context ctx = contextService.get(contextId).get();
		assertThat(ctx.getParams().containsKey("check_by_firstname")).isTrue();
		assertThat(ctx.getParams().get("check_by_firstname")).isEqualTo(true);
	}


	public static ProcessDefinition scenario4() {
		return createProcess("SIMPLE_SCENARIO_4", 0)
				.add(logic("CHECK_BY_FIRSTNAME").input("persons", expr("persons")).input("firstname", "firstname1").build())
				.build();
	}

	@Logic(id = "GET_FULLNAME", returnAs = "fullname")
	public static String getFullname(@Input(value = "person") Person person) {
		return person.getFirstname() + " " + person.getLastname();
	}

	@Logic(id = "SET_STATE", version = 0)
	public static void setState(@Input(value = "person", returned = true) Person person) {
		person.setState("BUSY");
	}
	
	@Logic(id = "COUNT_BUSY_STATES", returnAs = "busy_person_count")
	public static long countBusyStates(@Input(value = "persons") List<Person> persons) {
		return persons.stream().filter(p -> p.getState().equals("BUSY")).count();
	}
	
	@Logic(id = "COUNT_UNIQUE", returnAs = "unique_person_count")
	public static long countUnique(@Input(value = "persons") Set<Person> persons) {
		return persons.size();
	}
	
	@Logic(id = "CHECK_BY_FIRSTNAME", returnAs = "check_by_firstname")
	public static boolean checkByFirstname(@Input(value = "persons") Map<String, Person> persons, @Input("firstname") String firstname) {
		return persons.containsKey(firstname);
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	private static class Person {
		private String firstname;
		private String lastname;
		private String state;
	}
}
