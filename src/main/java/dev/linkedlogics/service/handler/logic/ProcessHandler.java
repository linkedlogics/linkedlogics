 package dev.linkedlogics.service.handler.logic;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.service.EvaluatorService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.TriggerService;
import dev.linkedlogics.service.handler.process.BranchFlowHandler;
import dev.linkedlogics.service.handler.process.CleanupFlowHandler;
import dev.linkedlogics.service.handler.process.CompensateFlowHandler;
import dev.linkedlogics.service.handler.process.DelayedFlowHandler;
import dev.linkedlogics.service.handler.process.DisabledFlowHandler;
import dev.linkedlogics.service.handler.process.EmptyCandidateFlowHandler;
import dev.linkedlogics.service.handler.process.EmptyFlowHandler;
import dev.linkedlogics.service.handler.process.ErrorFlowHandler;
import dev.linkedlogics.service.handler.process.ForcedFlowHandler;
import dev.linkedlogics.service.handler.process.ForkFlowHandler;
import dev.linkedlogics.service.handler.process.GroupFlowHandler;
import dev.linkedlogics.service.handler.process.HandlerResult;
import dev.linkedlogics.service.handler.process.JoinFlowHandler;
import dev.linkedlogics.service.handler.process.OutputFlowHandler;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler;
import dev.linkedlogics.service.handler.process.RetryFlowHandler;
import dev.linkedlogics.service.handler.process.SavepointFlowHandler;
import dev.linkedlogics.service.handler.process.SuccessFlowHandler;
import dev.linkedlogics.service.handler.process.VerifyFlowHandler;
import dev.linkedlogics.service.task.StartTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ProcessHandler extends LogicHandler {
	
	private ProcessFlowHandler waitingFlowHandler = new SuccessFlowHandler(new OutputFlowHandler());
	private ProcessFlowHandler lastFlowHandler = new EmptyFlowHandler(new SuccessFlowHandler(new OutputFlowHandler(new CleanupFlowHandler())));
	private ProcessFlowHandler nextFlowHandler = new EmptyFlowHandler(new EmptyCandidateFlowHandler(new ForcedFlowHandler(new DisabledFlowHandler(new DelayedFlowHandler(new VerifyFlowHandler(new SavepointFlowHandler(new JoinFlowHandler(new ForkFlowHandler(new GroupFlowHandler(new BranchFlowHandler()))))))))));
	private ProcessFlowHandler errorFlowHandler = new EmptyFlowHandler(new RetryFlowHandler(new ErrorFlowHandler(new CompensateFlowHandler())));
	
	
	public ProcessHandler() {

	}
	
	public ProcessHandler(LogicHandler handler) {
		super(handler);
	}

	@Override
	public void handle(LogicContext logicContext, Object result) {
		ServiceLocator.getInstance().getContextService().get(logicContext.getId()).ifPresent(context -> {
			transfer(logicContext, context);
			HandlerResult flowResult = getNextLogic(context);
			if (flowResult.isEndOfCandidates()) {
				log.info("context finished " + context.getId());
				context.setStatus(context.getError() == null ? Status.FINISHED : Status.FAILED);
				context.setFinishedAt(OffsetDateTime.now());
				ServiceLocator.getInstance().getContextService().set(context);
				ServiceLocator.getInstance().getCallbackService().publish(context);
			} else if (flowResult.getSelectedLogic().isPresent()) {
				SingleLogicDefinition logic = (SingleLogicDefinition) flowResult.getSelectedLogic().get();
				
				context.setLogicId(logic.getLogicId());
				context.setLogicVersion(logic.getLogicVersion());
				context.setLogicPosition(logic.getPosition());
				context.setLogicReturnAs(logic.getReturnAs());
				context.setSubmittedAt(OffsetDateTime.now());
				context.setApplication(logic.getApplication());
				
				final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
				Map<String, Object> inputs = new HashMap<>();
				logic.getInputMap().entrySet().stream().forEach(e -> {
					if (e.getValue() instanceof ExpressionLogicDefinition) {
						inputs.put(e.getKey(), evaluator.evaluate((ExpressionLogicDefinition) e.getValue(), context.getParams()));
					} else {
						inputs.put(e.getKey(), e.getValue());
					}
				});
				context.setInput(inputs);
				
//				context.setLogicStepCounter(context.getLogicStepCounter() + 1);
//				context.setSubmittedAt(OffsetDateTime.now());
				context.setOutput(null);
				context.setStatus(Status.STARTED);
//				context.setLogicExecutionTime(0);
//				context.setLogicTotalExecutionTime(0);
//				context.setExecutedAt(null);
				context.setUpdatedAt(OffsetDateTime.now());
				
				ServiceLocator.getInstance().getContextService().set(context);
				ServiceLocator.getInstance().getPublisherService().publish(LogicContext.fromLogic(context));
			}
		});
		
		super.handle(logicContext, result);
	}

	@Override
	public void handleError(LogicContext context, Throwable error) {
		handle(context, null);
	}
	
	private ProcessDefinition findProcess(String id, int version) {
		return ServiceLocator.getInstance().getProcessService().getProcess(id, version).orElseThrow(() -> new IllegalArgumentException(String.format("process %s[%d] is not found", id, version)));
	}
	
	public void transfer(LogicContext logicContext, Context context) {
		context.setLogicId(logicContext.getLogicId());
		context.setLogicVersion(logicContext.getLogicVersion());
		context.setLogicPosition(logicContext.getPosition());
		context.setOutput(logicContext.getOutput());
		context.setError(logicContext.getError() != null ? logicContext.getError() : context.getError());
		context.setExecutedAt(logicContext.getExecutedAt());
	}
	
	private HandlerResult getNextLogic(Context context) {
		ProcessDefinition process = findProcess(context.getProcessId(), context.getProcessVersion());
		
		HandlerResult result = handleLastLogic(context, process);
		int iteration = 0;
		while (result.getSelectedLogic().isEmpty()) {
			if (result.getNextCandidatePosition().get().equals(context.getEndPosition())) {
				return closeContext(context);
			}
			
			if (ProcessFlowHandler.LOG_ENTER) {
				log.info("ITERATION # "+ (++iteration));
			}
			Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			
			ProcessFlowHandler.tabs.clear();
			if (nextLogic.isPresent() && context.getError() != null) {
				if (ProcessFlowHandler.LOG_ENTER) log.info("ERROR HANDLERS");
				result = errorFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
				
				if (result.getNextCandidatePosition().isPresent()) {
					nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
				} else {
					nextLogic = result.getSelectedLogic();
					result = HandlerResult.nextCandidate(nextLogic.get().getPosition());
				}
			}
			ProcessFlowHandler.tabs.clear();
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
		context.setStatus(context.getError() == null ? Status.FINISHED : Status.FAILED);
		ServiceLocator.getInstance().getContextService().set(context);
		List<TriggerService.Trigger> triggers = ServiceLocator.getInstance().getTriggerService().get(context.getId());
		log.info("finished context " + context.getId() + " > " + triggers.size());
		if (triggers != null && !triggers.isEmpty()) {
			triggers.stream().forEach(t -> {
				ServiceLocator.getInstance().getProcessorService().process(new StartTask(LogicContext.fromTrigger(context, t)));
			});
		}
		
		return HandlerResult.noCandidate();
	}
	
	private HandlerResult handleLastLogic(Context context, ProcessDefinition process) {
		HandlerResult result = null;
		
		if (context.getStatus() == Status.INITIAL) {
			result = HandlerResult.nextCandidate(context.getStartPosition());
		} else if (context.getStatus() == Status.WAITING) {
			result = HandlerResult.nextCandidate(context.getLogicPosition());
			Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			waitingFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
		} else if (context.getStatus() == Status.SCHEDULED) {
			result = HandlerResult.nextCandidate(context.getLogicPosition());
		} else {
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
}
