package io.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.linkedlogics.service.ConfigurableService;
import io.linkedlogics.service.ProcessorService;
import io.linkedlogics.service.local.config.LocalProcessorServiceConfig;
import io.linkedlogics.service.task.LinkedLogicsTask;

public class LocalProcessorService extends ConfigurableService<LocalProcessorServiceConfig> implements ProcessorService {
	private ExecutorService service;
	
	public LocalProcessorService() {
		super(LocalProcessorServiceConfig.class);
	}
	
	@Override
	public void start() {
		Optional<Integer> threads = getConfig().getThreads();
		if (threads.isEmpty()) {
			service = Executors.newCachedThreadPool();
		} else {
			service = Executors.newFixedThreadPool(threads.get());
		}
	}
	
	@Override
	public void stop() {
		if (service != null) {
			service.shutdownNow();
		}
	}

	@Override
	public void process(LinkedLogicsTask task) {
		service.submit(task);
	}
}
