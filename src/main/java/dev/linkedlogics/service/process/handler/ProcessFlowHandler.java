package dev.linkedlogics.service.process.handler;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.LogicPositioner;

public abstract class ProcessFlowHandler {
	protected Optional<ProcessFlowHandler> nextHandler;
	
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
}