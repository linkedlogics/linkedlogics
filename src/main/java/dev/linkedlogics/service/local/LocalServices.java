package dev.linkedlogics.service.local;

import java.util.List;

import dev.linkedlogics.service.LinkedLogicsService;
import dev.linkedlogics.service.ServiceProvider;

public class LocalServices implements ServiceProvider {

	@Override
	public List<LinkedLogicsService> getStoringServices() {
		return List.of(new LocalLogicService(), new LocalProcessService(), new LocalContextService(), new LocalTriggerService());
	}

	@Override
	public List<LinkedLogicsService> getMessagingServices() {
		return List.of(new LocalQueueService(), new LocalPublisherService(), new LocalConsumerService());
	}

	@Override
	public List<LinkedLogicsService> getSchedulingServices() {
		return List.of(new LocalSchedulerService());
	}

	@Override
	public List<LinkedLogicsService> getProcessingServices() {
		return List.of(new LocalProcessorService(), new LocalAsyncService(), new LocalMapperService(), new LocalCallbackService());
	}

	@Override
	public List<LinkedLogicsService> getMonitoringServices() {
		return List.of(new LocalMetricsService(), new LocalLoggerService());
	}

	@Override
	public List<LinkedLogicsService> getEvaluatingServices() {
		return List.of(new LocalEvaluatorService());
	}
}
