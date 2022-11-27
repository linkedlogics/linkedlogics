package dev.linkedlogics.service.local;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.PublisherService;
import dev.linkedlogics.service.ServiceLocator;

public class LocalPublisherService implements PublisherService {

	@Override
	public void publish(Context context) {
		ServiceLocator.getInstance().getConsumerService().consume(context);
	}
}
