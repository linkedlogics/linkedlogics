package dev.linkedlogics.service;

import dev.linkedlogics.service.task.LinkedLogicsTask;

public interface ProcessorService extends LinkedLogicsService {
	
	void process(LinkedLogicsTask task);
}
