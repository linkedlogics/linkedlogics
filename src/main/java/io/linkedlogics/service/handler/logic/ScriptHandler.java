package io.linkedlogics.service.handler.logic;

import static io.linkedlogics.context.ContextLog.log;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

import io.linkedlogics.context.Context;
import io.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ScriptHandler extends LogicHandler {

	public ScriptHandler() {

	}
	
	public ScriptHandler(LogicHandler handler) {
		super(handler);
	}
	
	@Override
	public void handle(Context context, Object result) {
		context.setExecutedAt(OffsetDateTime.now());
		try {
			log(context).handler(this).inputs().message("executing script").info();
			String id = String.format("%s_%d_%s", context.getProcessId(), context.getProcessVersion(), context.getLogicPosition());
			Object scriptResult = null;
			if (context.getLogicReturnAs() != null) {
				scriptResult = ServiceLocator.getInstance().getEvaluatorService().evaluate(context.getLogicScript(), context.getParams());
				context.getOutput().put(context.getLogicReturnAs(), scriptResult);
			} else if (context.isLogicReturnAsMap()) {
				scriptResult = ServiceLocator.getInstance().getEvaluatorService().evaluateScript(context.getLogicScript(), id, context.getParams());
				context.getOutput().putAll((Map) scriptResult);
			}
			
			context.setExecutedIn(Duration.between(context.getExecutedAt(), OffsetDateTime.now()).toMillis());
			super.handle(context, scriptResult);	
		} catch (Exception e) {
			context.setExecutedIn(Duration.between(context.getExecutedAt(), OffsetDateTime.now()).toMillis());
			super.handleError(context, e);
		}
	}
}
