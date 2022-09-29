package dev.linkedlogics.service;

import dev.linkedlogics.context.LogicContext;

public interface ProcessorService extends LinkedLogicsService {
	
	boolean process(LogicContext context);
}
