package dev.linkedlogics.service.local;

import java.util.concurrent.ArrayBlockingQueue;

import dev.linkedlogics.config.LinkedLogicsConfiguration;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.ConsumerService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.task.ProcessorTask;

public class LocalConsumerService implements ConsumerService, Runnable {
	private Thread consumer;
	private boolean isRunning;
	private ArrayBlockingQueue<Context> queue;
	
	@Override
	public void start() {
		int queueSize = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.consumer.queue-size", 1000);
		queue = new ArrayBlockingQueue<>(queueSize);
		
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
				Context context = queue.take();
				ServiceLocator.getInstance().getProcessorService().process(new ProcessorTask(context));
			} catch (InterruptedException e) {}
		}
	}

	@Override
	public void consume(Context context) {
		queue.offer(context);
	}
}