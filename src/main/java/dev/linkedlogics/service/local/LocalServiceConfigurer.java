package dev.linkedlogics.service.local;

import dev.linkedlogics.service.SchedulerService;
import dev.linkedlogics.service.ServiceConfigurer;
import dev.linkedlogics.service.SchedulerService.Schedule;

public class LocalServiceConfigurer extends ServiceConfigurer {
	public LocalServiceConfigurer() {
		configure(new LocalLogicService());
		configure(new LocalProcessService());
		configure(new LocalProcessorService());
		configure(new LocalPublisherService());
		configure(new LocalConsumerService());
		configure(new LocalAsyncService());
		configure(new LocalContextService());
		configure(new LocalTriggerService());
		configure(new LocalMapperService());
		configure(new LocalEvaluatorService());
		configure(new LocalCallbackService());
		configure(new LocalMetricsService());
		configure(new LocalSchedulerService());
	}
}
