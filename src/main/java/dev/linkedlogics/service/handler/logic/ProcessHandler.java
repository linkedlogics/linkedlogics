 package dev.linkedlogics.service.handler.logic;

import java.time.OffsetDateTime;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextLog;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.handler.process.BranchFlowHandler;
import dev.linkedlogics.service.handler.process.CleanupFlowHandler;
import dev.linkedlogics.service.handler.process.CompensateFlowHandler;
import dev.linkedlogics.service.handler.process.DelayFlowHandler;
import dev.linkedlogics.service.handler.process.DisabledFlowHandler;
import dev.linkedlogics.service.handler.process.EmptyCandidateFlowHandler;
import dev.linkedlogics.service.handler.process.ErrorFlowHandler;
import dev.linkedlogics.service.handler.process.ExitFlowHandler;
import dev.linkedlogics.service.handler.process.FailFlowHandler;
import dev.linkedlogics.service.handler.process.ForcedFlowHandler;
import dev.linkedlogics.service.handler.process.ForkFlowHandler;
import dev.linkedlogics.service.handler.process.GroupFlowHandler;
import dev.linkedlogics.service.handler.process.HandlerResult;
import dev.linkedlogics.service.handler.process.JoinFlowHandler;
import dev.linkedlogics.service.handler.process.JumpFlowHandler;
import dev.linkedlogics.service.handler.process.OutputFlowHandler;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler;
import dev.linkedlogics.service.handler.process.RetryFlowHandler;
import dev.linkedlogics.service.handler.process.SavepointFlowHandler;
import dev.linkedlogics.service.handler.process.SuccessFlowHandler;
import dev.linkedlogics.service.handler.process.TimeoutFlowHandler;
import dev.linkedlogics.service.handler.process.VerifyFlowHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessHandler extends LogicHandler {
	private ProcessFlowHandler waitingFlowHandler = new SuccessFlowHandler(new OutputFlowHandler());
	private ProcessFlowHandler lastFlowHandler = new SuccessFlowHandler(new OutputFlowHandler(new CleanupFlowHandler()));
	private ProcessFlowHandler nextFlowHandler = new EmptyCandidateFlowHandler(new ForcedFlowHandler(new DisabledFlowHandler(new DelayFlowHandler(new VerifyFlowHandler(new SavepointFlowHandler(new JumpFlowHandler(new ExitFlowHandler(new FailFlowHandler(new TimeoutFlowHandler(new JoinFlowHandler(new ForkFlowHandler(new GroupFlowHandler(new BranchFlowHandler())))))))))))));
	private ProcessFlowHandler errorFlowHandler = new RetryFlowHandler(new ErrorFlowHandler(new CompensateFlowHandler()));

	public ProcessHandler() {
		setHandlersOrder();
	}
	
	public ProcessHandler(LogicHandler handler) {
		super(handler);
		setHandlersOrder();
	}
	
	private void setHandlersOrder() {
		waitingFlowHandler.setOrder(1);
		lastFlowHandler.setOrder(1);
		nextFlowHandler.setOrder(1);
		errorFlowHandler.setOrder(1);
	}

	@Override
	public void handle(Context context, Object result) {
		Optional<Context> maybeContext = ServiceLocator.getInstance().getContextService().get(context.getId());
		
		if (maybeContext.isPresent()) {
			Context fullContext = maybeContext.get();
			transfer(context, fullContext);
			HandlerResult handlerResult = handle(fullContext);
			log.debug(log(context, handlerResult.toString()).toString());
			
			if (handlerResult.isEndOfCandidates()) {
				fullContext.setStatus(context.getError() == null ? Status.FINISHED : Status.FAILED);
				fullContext.setFinishedAt(OffsetDateTime.now());
			}
			super.handle(fullContext, handlerResult);
		} else {
			super.handleError(context, new RuntimeException("context not found"));
		}
	}
	
	@Override
	public void handleError(Context context, Throwable error) {
		handle(context, null);
	}

	private HandlerResult handle(Context context) {
		ProcessDefinition process = findProcess(context.getProcessId(), context.getProcessVersion());
		
		HandlerResult result = handleLastLogic(context, process);
		while (result.getSelectedLogic().isEmpty()) {
			if (result.getNextCandidatePosition().get().equals(context.getEndPosition())) {
				return closeContext(context);
			}
			
			Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			
			if (nextLogic.isPresent() && context.getError() != null) {
				result = errorFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
				
				if (result.getNextCandidatePosition().isPresent()) {
					nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
				} else if (result.getSelectedLogic().isPresent()) {
					nextLogic = result.getSelectedLogic();
					result = HandlerResult.nextCandidate(nextLogic.get().getPosition());
				} else {
					return result;
				}
			}
			result = nextFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
			if (result.getNextCandidatePosition().isEmpty()) {
				return result;
			}
		}
		
		if (result.getSelectedLogic().isPresent()) {
			return result;
		}
		
		return HandlerResult.noCandidate();
	}
	
	private HandlerResult closeContext(Context context) {
		return HandlerResult.endOfCandidates();
	}
	
	private HandlerResult handleLastLogic(Context context, ProcessDefinition process) {
		HandlerResult result = null;
		
		if (context.getStatus() == Status.INITIAL) {
			log.debug(log(context, "context is initial state").toString());
			result = HandlerResult.nextCandidate(context.getStartPosition());
		} else if (context.getStatus() == Status.WAITING) {
			log.debug(log(context, "context is waiting state").toString());
			result = HandlerResult.nextCandidate(context.getLogicPosition());
			Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			waitingFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
		} else if (context.getStatus() == Status.SCHEDULED) {
			log.debug(log(context, "context is scheduled state").toString());
			result = HandlerResult.nextCandidate(context.getLogicPosition());
		} else {
			log.debug(log(context, "context is in progress").toString());
			result = HandlerResult.nextCandidate(context.getLogicPosition());
			while (result.getNextCandidatePosition().isPresent()) {
				Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
				result = lastFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
				if (result.getSelectedLogic().isPresent()) {
					break;
				} 
			}
			
			if (context.getError() != null) {
				result = HandlerResult.nextCandidate(context.getLogicPosition());
			} else {
				result = HandlerResult.nextCandidate(ProcessFlowHandler.adjacentLogicPosition(context.getLogicPosition()));
			}
		}

		return result;
	}
	
	private ProcessDefinition findProcess(String id, int version) {
		return ServiceLocator.getInstance().getProcessService().getProcess(id, version).orElseThrow(() -> new IllegalArgumentException(String.format("process %s[%d] is not found", id, version)));
	}
	
	public void transfer(Context logicContext, Context context) {
		context.setLogicId(logicContext.getLogicId());
		context.setLogicVersion(logicContext.getLogicVersion());
		context.setLogicPosition(logicContext.getLogicPosition());
		context.setOutput(logicContext.getOutput());
		context.setError(logicContext.getError() != null ? logicContext.getError() : context.getError());
		context.setExecutedAt(logicContext.getExecutedAt());
	}
	
	private ContextLog log(Context context, String message) {
		return ContextLog.builder(context)
				.handler(this.getClass().getSimpleName())
				.message(message)
				.build();
	}
}
