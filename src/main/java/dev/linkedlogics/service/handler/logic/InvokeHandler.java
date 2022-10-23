package dev.linkedlogics.service.handler.logic;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.model.LogicDefinition;
import dev.linkedlogics.model.parameter.CollectionParameter;
import dev.linkedlogics.model.parameter.MapParameter;
import dev.linkedlogics.service.MapperService;
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
		return Arrays.stream(logic.getParameters())
				.map(p -> {
					ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
					Object value = p.getParameterValue(context);
					if (value instanceof Collection && p instanceof CollectionParameter) {
						CollectionParameter collectionParameter = (CollectionParameter) p;
						return mapper.convertValue(value, mapper.getTypeFactory().constructCollectionType((Class<? extends Collection>) p.getType(), collectionParameter.getGenericType()));
 					} else if (value instanceof Map<?, ?> && p instanceof MapParameter) {
 						MapParameter mapParameter = (MapParameter) p;
						return mapper.convertValue(value, mapper.getTypeFactory().constructMapType((Class<? extends Map>) value.getClass(), mapParameter.getKeyType(), mapParameter.getValueType()));
 					}
					return mapper.convertValue(value, p.getType());
				})
				.toArray();
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
