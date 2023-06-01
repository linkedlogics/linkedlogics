package io.linkedlogics.service.local;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.linkedlogics.service.ProcessorService;
import io.linkedlogics.service.config.ServiceConfiguration;
import io.linkedlogics.service.local.config.LocalProcessorServiceConfig;
import io.linkedlogics.service.task.LinkedLogicsTask;

public class LocalProcessorService implements ProcessorService {
	private ExecutorService service;
	private LocalProcessorServiceConfig config = new ServiceConfiguration().getConfig(LocalProcessorServiceConfig.class);
	
	@Override
	public void start() {
		Optional<Integer> threads = config.getThreads();
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
