package io.linkedlogics.service.common;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.context.Context;
import io.linkedlogics.service.CallbackService;
import io.linkedlogics.service.ConfigurableService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.TopicService;
import io.linkedlogics.service.local.config.LocalCallbackServiceConfig;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class QueueCallbackService extends ConfigurableService<LocalCallbackServiceConfig> implements CallbackService, Runnable {
	private final static String CALLBACK_QUEUE = "callback";
	private ConcurrentHashMap<String, LinkedLogicsCallback> callbackMap;
	private ScheduledExecutorService scheduler;
	private Thread consumer;
	private boolean isRunning;

	public QueueCallbackService() {
		super(LocalCallbackServiceConfig.class);
	}
	
	@Override
	public void start() {
		callbackMap = new ConcurrentHashMap<>();
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
		scheduler.schedule(() -> callback.onTimeout(), getConfig().getExpireTimeOrDefault(5), TimeUnit.SECONDS);
	}

	@Override
	public void publish(Context context) {
		TopicService topicService = ServiceLocator.getInstance().getTopicService();
		topicService.offer(CALLBACK_QUEUE, toString(context));
	}
	
	@Override
	public void run() {
		isRunning = true;
		
		while (isRunning) {
			try {
				TopicService topicService = ServiceLocator.getInstance().getTopicService();
				Optional<String> message = topicService.poll(CALLBACK_QUEUE);
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
