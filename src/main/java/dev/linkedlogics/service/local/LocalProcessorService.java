package dev.linkedlogics.service.local;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dev.linkedlogics.config.LinkedLogicsConfiguration;
import dev.linkedlogics.service.ProcessorService;
import dev.linkedlogics.service.task.LinkedLogicsTask;

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
