package dev.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import dev.linkedlogics.config.LinkedLogicsConfiguration;
import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.CallbackService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.task.CallbackExpireTask;
import dev.linkedlogics.service.task.CallbackTask;

public class LocalCallbackService implements CallbackService {
	private ConcurrentHashMap<String, LogicContext> contextMap = new ConcurrentHashMap<>();
	private ScheduledExecutorService scheduler;
	private ThreadLocal<String> contextId = new ThreadLocal<>();
	
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
	public void set(LogicContext context) {
		contextMap.put(context.getId(), context);
		scheduler.schedule(new CallbackExpireTask(context), expireTime, TimeUnit.SECONDS);
	}

	@Override
	public Optional<LogicContext> remove(String contextId) {
		return Optional.ofNullable(contextMap.remove(contextId));
	}

	@Override
	public void callback(String contextId, Object result) {
		if (contextMap.containsKey(contextId)) {
			remove(contextId).ifPresent(c -> {
				ServiceLocator.getInstance().getProcessorService().process(new CallbackTask(c, result));
			});
		}
	}

	@Override
	public void setContextId(String contextId) {
		this.contextId.set(contextId);
	}
	
	@Override
	public void unsetContextId() {
		this.contextId.remove();
	}

	@Override
	public String getContextId() {
		return this.contextId.get();
	}
}
