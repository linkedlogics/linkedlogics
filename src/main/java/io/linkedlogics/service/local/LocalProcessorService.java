package io.linkedlogics.service.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.linkedlogics.config.LinkedLogicsConfiguration;
import io.linkedlogics.service.ProcessorService;
import io.linkedlogics.service.task.LinkedLogicsTask;

public class LocalProcessorService implements ProcessorService {
	private ExecutorService service;
	
	@Override
	public void start() {
		int threads = (Integer) LinkedLogicsConfiguration.getConfigOrDefault("services.processor.threads", -1);
		if (threads == -1) {
			service = Executors.newCachedThreadPool();
		} else {
			service = Executors.newFixedThreadPool(threads);
		}
	}
	
	@Override
	public void stop() {
		service.shutdownNow();
	}

	@Override
	public void process(LinkedLogicsTask task) {
		service.submit(task);
	}
}
