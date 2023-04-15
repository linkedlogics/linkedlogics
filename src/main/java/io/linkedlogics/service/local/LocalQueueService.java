package io.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.config.LinkedLogicsConfiguration;
import io.linkedlogics.service.QueueService;

public class LocalQueueService implements QueueService {
	private ConcurrentHashMap<String, ArrayBlockingQueue<String>> queueMap;
	private int queueSize;
	
	public LocalQueueService() {
		queueMap = new ConcurrentHashMap<>();
		queueSize = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.consumer.queue-size", 1000);
		queueMap.put(LinkedLogics.getApplicationName(), new ArrayBlockingQueue<>(queueSize));
	}
	
	@Override
	public void offer(String queue, String payload) {
		ArrayBlockingQueue<String> q = queueMap.getOrDefault(queue, new ArrayBlockingQueue<>(queueSize));
		if (!queueMap.containsKey(queue)) {
			queueMap.put(queue, q);
		}
		
		q.offer(payload);
	}

	@Override
	public Optional<String> poll(String queue) {
		try {
			if (queueMap.containsKey(queue)) {
				return Optional.of(queueMap.get(queue).take());
			}
		} catch (InterruptedException e) {
			return Optional.empty();
		}
		
		return Optional.empty();
	}
}
