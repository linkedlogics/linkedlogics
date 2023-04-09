package dev.linkedlogics.service.handler.process;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextLog;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.helper.LogicPositioner;
import dev.linkedlogics.service.handler.logic.ProcessHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProcessFlowHandler {
	protected enum Flow {
		CONTINUE("->"),
		RESET("<<"),
		TERMINATE("=#");
		
		String symbol;
		
		private Flow(String symbol) {
			this.symbol = symbol;
		}
		
		public String getSymbol() {
			return this.symbol;
		}
	}
	
	protected Optional<ProcessFlowHandler> nextHandler;
	@Getter
	protected Integer order;
	
	public ProcessFlowHandler() {
		this.nextHandler = Optional.empty();
	}
	
	public ProcessFlowHandler(ProcessFlowHandler nextHandler) {
		this.nextHandler = Optional.of(nextHandler);
	}
	
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		return nextHandler.map(h -> h.handle(candidate, candidatePosition, context)).orElse(HandlerResult.selectCandidate(candidate));
	}
	
	public static String adjacentLogicPosition(String position) {
		if (position.endsWith(LogicPositioner.COMPENSATE)) {
			return position;
		}
		
		String[] positions = position.split("\\.");
		positions[positions.length - 1] = String.valueOf(Integer.parseInt(clearLogicPosition(positions[positions.length - 1])) + 1);
		return Arrays.stream(positions).collect(Collectors.joining("."));
	}
	
	public static String parentLogicPosition(String position) {
		String[] positions = position.split("\\.");
		return Arrays.stream(positions).limit(positions.length - 1).collect(Collectors.joining("."));
	}
	
	public static String clearLogicPosition(String position) {
		return position.replaceAll("[^0-9]", "");
	}
	
	protected void log(Context context, String message, String candidate, Flow flow) {
		ContextLog contextLog = ContextLog.builder(context)
				.handler(ProcessHandler.class.getSimpleName() + "." + this.getClass().getSimpleName())
				.step(String.format("%2d.%s", getOrder(), flow.getSymbol()))
				.candidate(candidate)
				.message(message)
				.build();
		
		log.trace(contextLog.toString());
	}
	
	public void setOrder(int order) {
		this.order = order;
		nextHandler.ifPresent(h -> h.setOrder(order + 1));
	}
	
	public int getOrder() {
		return this.order;
	}
}