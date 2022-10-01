package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.service.handler.logic.LogicHandler;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class LinkedLogicsTask implements Runnable {
	protected LogicContext context;
	protected LogicHandler handler;
}
