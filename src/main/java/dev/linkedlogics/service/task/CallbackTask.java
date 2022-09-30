package dev.linkedlogics.service.task;

import dev.linkedlogics.context.LogicContext;
import lombok.Getter;

@Getter
public class CallbackTask extends LinkedLogicsTask {
	private Object result;
	
	public CallbackTask(LogicContext context, Object result) {
		super(context);
		this.result = result;
	}

	@Override
	public void run() {
		
	}
}
