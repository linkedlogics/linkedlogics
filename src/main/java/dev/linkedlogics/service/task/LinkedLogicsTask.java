package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class LinkedLogicsTask implements Runnable {
	private LogicContext context;
}
