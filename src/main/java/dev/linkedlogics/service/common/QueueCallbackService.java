package dev.linkedlogics.service.common;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.LinkedLogicsCallback;
import dev.linkedlogics.config.LinkedLogicsConfiguration;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.service.CallbackService;
import dev.linkedlogics.service.QueueService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.TopicService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueCallbackService implements CallbackService, Runnable {
	public static final String CALLBACK = "callback";
	
	private ConcurrentHashMap<String, LinkedLogicsCallback> callbackMap = new ConcurrentHashMap<>();
	private ScheduledExecutorService scheduler;
	private Thread consumer;
	private boolean isRunning;
	private int expireTime;
	
	@Override
	public void start() {
		expireTime = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.callback.expire-time", 5);
		scheduler = Executors.newSingleThreadScheduledExecutor();
		consumer = new Thread(this);
		consumer.start();
	}

	@Override
	public void stop() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
		isRunning = false;
		if (consumer != null) {
			consumer.interrupt();
		}
		
		if (!callbackMap.isEmpty()) {
			callbackMap.entrySet().forEach(c -> {
				try {
					c.getValue().onTimeout();
				} catch (Exception e) {
					log.error(e.getLocalizedMessage(), e);
				}
			});
		}
	}

	@Override
	public void set(String contextId, LinkedLogicsCallback callback) {
		callbackMap.put(contextId, callback);
		scheduler.schedule(() -> callback.onTimeout(), expireTime, TimeUnit.SECONDS);
	}

	@Override
	public void publish(Context context) {
		TopicService topicService = ServiceLocator.getInstance().getTopicService();
		topicService.offer(CALLBACK, toString(context));
	}
	
	@Override
	public void run() {
		isRunning = true;
		
		while (isRunning) {
			try {
				TopicService topicService = ServiceLocator.getInstance().getTopicService();
				Optional<String> message = topicService.poll(CALLBACK);
				if (message.isPresent()) {
					try {
						callback(fromString(message.get()));
					} catch (Exception e) {
						log.error(e.getLocalizedMessage(), e);
					}
				} else {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {}
		}
	}
	
	public void callback(Context context) {
		Optional.ofNullable(callbackMap.remove(context.getId())).ifPresent((c) -> {
			if (context.getError() == null) {
				c.onSuccess(context);
			} else {
				c.onFailure(context, context.getError());
			}
		});
	}
	
	private String toString(Context context) {
		try {
			return ServiceLocator.getInstance().getMapperService().getMapper().writeValueAsString(context);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	private Context fromString(String string) {
		if (string == null || string.length() == 0) {
			return null;
		}
		
		ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
		try {
			return mapper.readValue(string, Context.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
