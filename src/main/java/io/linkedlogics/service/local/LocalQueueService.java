package io.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.service.ConfigurableService;
import io.linkedlogics.service.QueueService;
import io.linkedlogics.service.local.config.LocalQueueServiceConfig;

public class LocalQueueService extends ConfigurableService<LocalQueueServiceConfig> implements QueueService {
	private ConcurrentHashMap<String, ArrayBlockingQueue<String>> queueMap = new ConcurrentHashMap<>();
	
	public LocalQueueService() {
		super(LocalQueueServiceConfig.class);
	}

	@Override
	public void start() {
		queueMap.put(LinkedLogics.getApplicationName(), new ArrayBlockingQueue<>(getConfig().getQueueSize(100000)));
	}
	
	@Override
	public void offer(String queue, String payload) {
		ArrayBlockingQueue<String> q = queueMap.getOrDefault(queue, new ArrayBlockingQueue<>(getConfig().getQueueSize(100000)));
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
