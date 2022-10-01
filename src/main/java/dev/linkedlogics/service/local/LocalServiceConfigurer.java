package dev.linkedlogics.service.local;

import dev.linkedlogics.service.ServiceConfigurer;

public class LocalServiceConfigurer extends ServiceConfigurer {
	public LocalServiceConfigurer() {
		configure(new LocalLogicService());
		configure(new LocalProcessService());
		configure(new LocalProcessorService());
		configure(new LocalPublisherService());
		configure(new LocalConsumerService());
		configure(new LocalCallbackService());
		configure(new LocalContextService());
		configure(new LocalTriggerService());
		configure(new LocalMapperService());
		configure(new LocalEvaluatorService());
	}
}
