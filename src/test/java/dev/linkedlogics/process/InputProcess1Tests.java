package dev.linkedlogics.process;

import static dev.linkedlogics.LinkedLogicsBuilder.createProcess;
import static dev.linkedlogics.LinkedLogicsBuilder.expr;
import static dev.linkedlogics.LinkedLogicsBuilder.logic;
import static dev.linkedlogics.process.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.annotation.Logic;
import dev.linkedlogics.annotation.ProcessChain;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.service.ContextService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.local.LocalServiceConfigurer;

public class InputProcess1Tests {

	private static ContextService contextService;

	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new LocalServiceConfigurer());
		LinkedLogics.registerLogic(InputProcess1Tests.class);
		LinkedLogics.registerProcess(InputProcess1Tests.class);
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


	@ProcessChain
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("GET_FULLNAME").input("person", expr("person")).build())
				.add(logic("SET_STATE").input("person", expr("person")).build())
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
	
	private static class Person {
		private String firstname;
		private String lastname;
		private String state;

		public Person() {
			
		}
		
		public Person(String firstname, String lastname, String state) {
			this.firstname = firstname;
			this.lastname = lastname;
			this.state = state;
		}

		public String getFirstname() {
			return firstname;
		}

		public void setFirstname(String firstname) {
			this.firstname = firstname;
		}

		public String getLastname() {
			return lastname;
		}

		public void setLastname(String lastname) {
			this.lastname = lastname;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}
	}
}
