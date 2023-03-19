package dev.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;

import dev.linkedlogics.config.LinkedLogicsConfiguration;
import dev.linkedlogics.service.QueueService;

public class LocalQueueService implements QueueService {

	private ArrayBlockingQueue<String> queue;
	
	public LocalQueueService() {
		int queueSize = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.consumer.queue-size", 1000);
		queue = new ArrayBlockingQueue<>(queueSize);
	}
	
	@Override
	public void offer(String queue, String payload) {
		this.queue.offer(payload);
	}

	@Override
	public Optional<String> poll(String queue) {
		try {
			return Optional.of(this.queue.take());
		} catch (InterruptedException e) {
			return Optional.empty();
		}
	}
}
