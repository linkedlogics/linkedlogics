package io.linkedlogics.service.common;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.context.Context;
import io.linkedlogics.service.ConsumerService;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.common.config.QueueConsumerServiceConfig;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.task.ProcessorTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueConsumerService implements ConsumerService, Runnable {
	private Thread consumer;
	private boolean isRunning;
	private QueueConsumerServiceConfig config = new ServiceConfiguration().getConfig(QueueConsumerServiceConfig.class);
	
	@Override
	public void start() {
		consumer = new Thread(this);
		consumer.start();
	}

	@Override
	public void stop() {
		isRunning = false;
		if (consumer != null) {
			consumer.interrupt();
		}
	}

	@Override
	public void run() {
		isRunning = true;
		
		while (isRunning) {
			try {
				QueueService queueService = ServiceLocator.getInstance().getQueueService();
				Optional<String> message = queueService.poll(LinkedLogics.getApplicationName());
				if (message.isPresent()) {
					ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
					try {
						consume(mapper.readValue(message.get(), Context.class));
					} catch (Exception e) {
						log.error(e.getLocalizedMessage(), e);
					}
				} else {
					Thread.sleep(config.getDelay(1));
				}
			} catch (InterruptedException e) {}
		}
	}

	@Override
	public void consume(Context context) {
		ServiceLocator.getInstance().getProcessorService().process(new ProcessorTask(context));
	}
}