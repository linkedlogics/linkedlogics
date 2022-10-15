package dev.linkedlogics.service.handler.logic;

import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.stream.IntStream;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.model.LogicDefinition;
import dev.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class InvokeHandler extends LogicHandler {

	public InvokeHandler() {

	}
	
	public InvokeHandler(LogicHandler handler) {
		super(handler);
	}
	
	@Override
	public void handle(LogicContext context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		context.setExecutedAt(OffsetDateTime.now());
		try {
			if (logic.isReturnAsync()) {
				ServiceLocator.getInstance().getAsyncService().set(context);
			}
			
			log.info(String.format("> %-10s%s", context.getPosition(), context.getLogicId()));
			Object methodResult = invokeMethod(context, logic, getInvokeParams(context, logic));
			context.setExecutedIn(Duration.between(context.getExecutedAt(), OffsetDateTime.now()).toMillis());
			super.handle(context, methodResult);	
		} catch (Exception e) {
			context.setExecutedIn(Duration.between(context.getExecutedAt(), OffsetDateTime.now()).toMillis());
			super.handleError(context, e);
		}
	}
	
	protected Object[] getInvokeParams(LogicContext context, LogicDefinition logic) {
		return Arrays.stream(logic.getParameters()).map(p -> p.getParameterValue(context)).toArray();
	}
	
	protected int[] getInvokeReturnedParamIndexes(LogicContext context, LogicDefinition logic) {
		return IntStream.range(0, logic.getParameters().length).filter(i -> logic.getParameters()[i].isReturned()).toArray();
	}
	
	protected Object invokeMethod(LogicContext context, LogicDefinition logic, Object[] params) throws Exception {
		Object result = null;
		logic.getMethod().setAccessible(true);
		
		if (Modifier.isStatic(logic.getMethod().getModifiers())) {
			result = logic.getMethod().invoke(null, params);
		} else {
			result = logic.getMethod().invoke(logic.getObject(), params);
		}
		
		Arrays.stream(getInvokeReturnedParamIndexes(context, logic)).forEach(i -> {
			context.getOutput().put(logic.getParameters()[i].getName(), params[i]);
		});
		
		return result;
	}
}
