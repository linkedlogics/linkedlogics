package dev.linkedlogics.service.local;

import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.ConsumerService;
import dev.linkedlogics.service.QueueService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.task.ProcessorTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalConsumerService implements ConsumerService, Runnable {
	private Thread consumer;
	private boolean isRunning;
	
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
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {}
		}
	}

	@Override
	public void consume(Context context) {
		ServiceLocator.getInstance().getProcessorService().process(new ProcessorTask(context));
	}
}