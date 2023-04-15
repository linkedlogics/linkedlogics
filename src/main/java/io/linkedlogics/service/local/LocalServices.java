package io.linkedlogics.service.local;

import java.util.List;

import io.linkedlogics.service.LinkedLogicsService;
import io.linkedlogics.service.ServiceProvider;

public class LocalServices extends ServiceProvider {

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
		return List.of(new LocalMetricsService(), new LocalTrackerService());
	}

	@Override
	public List<LinkedLogicsService> getEvaluatingServices() {
		return List.of(new LocalEvaluatorService());
	}

	@Override
	public List<LinkedLogicsService> getTrackingServices() {
		return List.of(new LocalTrackerService());
	}
	
	
}
