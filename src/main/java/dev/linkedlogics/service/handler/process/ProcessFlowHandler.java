package dev.linkedlogics.service.handler.process;

import java.util.Arrays;
import java.util.Optional;
import java.util.Stack;
import java.util.stream.Collectors;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.LogicPositioner;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.service.ServiceLocator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class ProcessFlowHandler {
	protected Optional<ProcessFlowHandler> nextHandler;
	
	public ProcessFlowHandler() {
		this.nextHandler = Optional.empty();
	}
	
	public ProcessFlowHandler(ProcessFlowHandler nextHandler) {
		this.nextHandler = Optional.of(nextHandler);
	}
	
	public static Stack<String> tabs = new Stack<>();
	
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		try {
			tabs.push("\t");
			nextHandler.ifPresent(h -> enter(h, candidate, candidatePosition, context));
			
			return nextHandler.map(h -> {
				HandlerResult result = h.handle(candidate, candidatePosition, context);
				exit("EXIT", result);
				return result;
			}).orElse(HandlerResult.selectCandidate(candidate));
		} finally {
			try {
				tabs.pop();
			} catch (Exception e) { }
		}
	}
	
	protected ProcessDefinition findProcess(Context context) {
		return ServiceLocator.getInstance().getProcessService().getProcess(context.getProcessId(), context.getProcessVersion()).orElseThrow(() -> new IllegalArgumentException(String.format("process %s[%d] is not found", context.getProcessId(), context.getProcessVersion())));
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
	
	public static boolean LOG_ENTER = false;
	public static boolean LOG_EXIT = false;
	public static Stack<Class<?>> HANDLERS = new Stack<>();
	
	public static void enter(ProcessFlowHandler handler, Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (LOG_ENTER) {
			HANDLERS.push(handler.getClass());
			String t = tabs.stream().collect(Collectors.joining());
			log.info(String.format("%s > %-36s %s", t, handler.getClass().getSimpleName(), toString(candidate, candidatePosition, context)));
		}
	}
	
	public static void exit(String type, HandlerResult result) {
		if (LOG_EXIT && !tabs.isEmpty()) {
			String t = tabs.stream().collect(Collectors.joining());
			log.info(String.format("%s < %-36s %-24s result=%s", t, HANDLERS.pop().getSimpleName(), type, result.toString()));
		}
	}
	
	public static String toString(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		StringBuilder  s = new StringBuilder();
		
		if (candidate != null && candidate.isPresent()) {
			s.append("CANDIDATE [").append(candidate.get().getPosition() + "] ");
		}
		
		if (candidatePosition != null) {
			s.append("CANDIDATE_POS[").append(candidatePosition).append("]");
		}

		if (context.getError() != null) {
			s.append(" ERROR ");
		}
		
		return s.toString();
	}
}