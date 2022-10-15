package dev.linkedlogics.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.context.LogicContext;

@ExtendWith(MockitoExtension.class)
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
	
	@Test
	@DisplayName("should start and stop service")
	public void shouldStartAndStopService() {
		ServiceConfigurer configurer = new ServiceConfigurer();
		LinkedLogicsService service = Mockito.mock(LinkedLogicsService.class);
		configurer.configure(service);
		
		ServiceLocator.getInstance().configure(configurer);
		ServiceLocator.getInstance().shutdown();
		
		verify(service, times(1)).start();
		verify(service, times(1)).stop();
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
		public void  publish(LogicContext context) {

		}
	}
	
	public static class PublisherB implements PublisherService {
		
		@Override
		public void publish(LogicContext context) {

		}
	}
	
	public static class ConsumerA implements ConsumerService {

		@Override
		public void consume(LogicContext context) {

		}
	}
}
