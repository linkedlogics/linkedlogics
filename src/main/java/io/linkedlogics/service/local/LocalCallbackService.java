package io.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.linkedlogics.LinkedLogicsCallback;
import io.linkedlogics.config.LinkedLogicsConfiguration;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;
import io.linkedlogics.service.CallbackService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalCallbackService implements CallbackService {
	private ConcurrentHashMap<String, LinkedLogicsCallback> callbackMap = new ConcurrentHashMap<>();
	private ScheduledExecutorService scheduler;
	
	private int expireTime;
	
	@Override
	public void start() {
		expireTime = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.callback.expire-time", 5);
		scheduler = Executors.newSingleThreadScheduledExecutor();
	}

	@Override
	public void stop() {
		if (scheduler != null) {
			scheduler.shutdownNow();
		}
	}

	@Override
	public void set(String contextId, LinkedLogicsCallback callback) {
		callbackMap.put(contextId, callback);
		scheduler.schedule(() -> callback.onTimeout(), expireTime, TimeUnit.SECONDS);
	}

	@Override
	public void publish(Context context) {
		Optional.ofNullable(callbackMap.remove(context.getId())).ifPresent((c) -> {
			if (context.getStatus() == Status.FINISHED) {
				c.onSuccess(context);
			} else if (context.getStatus() == Status.FAILED){
				c.onFailure(context, context.getError());
			} else if (context.getStatus() == Status.CANCELLED) {
				c.onCancellation(context);
			}
		});
	}
}
