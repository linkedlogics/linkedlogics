package dev.linkedlogics.service;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.context.LogicContext;

public class ServiceLocatorTests {
	
	@BeforeAll
	public static void setUp() {
		LinkedLogics.configure(new ConfigurerA().configure(new ConfigurerB()));
	}
	
	@Test
	@DisplayName("should return last configured service")
	public void shouldReturnCorrectService() {
		assertThat(ServiceLocator.getInstance().getPublisherService().getClass()).isEqualTo(PublisherB.class);
		assertThat(ServiceLocator.getInstance().getConsumerService().getClass()).isEqualTo(ConsumerA.class);
	}
	
	public static class ConfigurerA extends ServiceConfigurer {
		public ConfigurerA() {
			configure(new PublisherA());
			configure(new ConsumerA());
		}
	}
	
	public static class ConfigurerB extends ServiceConfigurer {
		public ConfigurerB() {
			configure(new PublisherB());
		}
	}
	
	public static class PublisherA implements PublisherService {

		@Override
		public boolean publish(LogicContext context) {
			return false;
		}
	}
	
	public static class PublisherB implements PublisherService {
		
		@Override
		public boolean publish(LogicContext context) {
			return false;
		}
	}
	
	public static class ConsumerA implements ConsumerService {

		@Override
		public boolean consume(LogicContext context) {
			return false;
		}
	}
}
