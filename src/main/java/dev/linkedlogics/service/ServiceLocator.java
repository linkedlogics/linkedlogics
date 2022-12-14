package dev.linkedlogics.service;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceLocator {
	private static final ServiceLocator INSTANCE = new ServiceLocator();
	
	private Map<Class<?>, LinkedLogicsService> services = new HashMap<>();
	
	public static ServiceLocator getInstance() {
		return INSTANCE;
	}
	
	public void configure(ServiceConfigurer configurer) {
		configurer.getServices().entrySet().forEach(e -> {
			if (this.services.containsKey(e.getKey())) {
				this.services.remove(e.getKey()).stop();
			}
			this.services.put(e.getKey(), e.getValue());
		});
		configurer.getServices().values().stream().collect(Collectors.toSet()).stream().forEach(s -> s.start());
	}
	
	public <T> T getService(Class<T> serviceClass) {
		return (T) services.get(serviceClass);
	}
	
	public ConsumerService getConsumerService() {
		return getService(ConsumerService.class);
	}
	
	public PublisherService getPublisherService() {
		return getService(PublisherService.class);
	}
	
	public ProcessorService getProcessorService() {
		return getService(ProcessorService.class);
	}
	
	public SchedulerService getSchedulerService() {
		return getService(SchedulerService.class);
	}
	
	public EvaluatorService getEvaluatorService() {
		return getService(EvaluatorService.class);
	}
	
	public AsyncService getAsyncService() {
		return getService(AsyncService.class);
	}
	
	public ContextService getContextService() {
		return getService(ContextService.class);
	}
	
	public LogicService getLogicService() {
		return getService(LogicService.class);
	}
	
	public ProcessService getProcessService() {
		return getService(ProcessService.class);
	}
	
	public TriggerService getTriggerService() {
		return getService(TriggerService.class);
	}
	
	public MapperService getMapperService() {
		return getService(MapperService.class);
	}
	
	public CallbackService getCallbackService() {
		return getService(CallbackService.class);
	}
	
	public MetricsService getMetricsService() {
		return getService(MetricsService.class);
	}
	
	public void shutdown() {
		services.values().stream().collect(Collectors.toSet()).stream().forEach(s -> s.stop());
	}
}
