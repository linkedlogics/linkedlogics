package dev.linkedlogics.service.logic.handler;

import java.lang.reflect.Modifier;
import java.util.Arrays;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.model.LogicDefinition;

public class InvokeHandler extends LogicHandler {

	public InvokeHandler() {

	}
	
	public InvokeHandler(LogicHandler handler) {
		super(handler);
	}
	
	@Override
	public void handle(LogicContext context, Object result) {
		LogicDefinition logic = findLogic(context.getLogicId(), context.getLogicVersion());
		try {
			System.out.println(String.format("> %-10s%s", context.getPosition(), context.getLogicId()));
			Object methodResult = invokeMethod(context, logic, getInvokeParams(context, logic));
			
			if (!logic.isReturnAsync()) {
				super.handle(context, methodResult);	
			}
		} catch (Exception e) {
			super.handleError(context, e);
		}
	}
	
	protected Object[] getInvokeParams(LogicContext context, LogicDefinition logic) {
		return Arrays.stream(logic.getParameters()).map(p -> p.getParameterValue(context)).toArray();
	}
	
	protected Object invokeMethod(LogicContext context, LogicDefinition logic, Object[] params) throws Exception {
		Object result = null;
		if (Modifier.isStatic(logic.getMethod().getModifiers())) {
			result = logic.getMethod().invoke(null, params);
		} else {
			result = logic.getMethod().invoke(logic.getObject(), params);
		}
		return result;
	}
}
