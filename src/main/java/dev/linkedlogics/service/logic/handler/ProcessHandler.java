 package dev.linkedlogics.service.logic.handler;

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
import dev.linkedlogics.service.process.handler.BranchFlowHandler;
import dev.linkedlogics.service.process.handler.CleanupFlowHandler;
import dev.linkedlogics.service.process.handler.CompensateFlowHandler;
import dev.linkedlogics.service.process.handler.DisabledFlowHandler;
import dev.linkedlogics.service.process.handler.EmptyCandidateFlowHandler;
import dev.linkedlogics.service.process.handler.ErrorFlowHandler;
import dev.linkedlogics.service.process.handler.ForcedFlowHandler;
import dev.linkedlogics.service.process.handler.ForkFlowHandler;
import dev.linkedlogics.service.process.handler.GroupFlowHandler;
import dev.linkedlogics.service.process.handler.HandlerResult;
import dev.linkedlogics.service.process.handler.JoinFlowHandler;
import dev.linkedlogics.service.process.handler.OutputFlowHandler;
import dev.linkedlogics.service.process.handler.ProcessFlowHandler;
import dev.linkedlogics.service.process.handler.RetryFlowHandler;
import dev.linkedlogics.service.process.handler.SuccessFlowHandler;
import dev.linkedlogics.service.process.handler.VerifyFlowHandler;
import dev.linkedlogics.service.task.StartTask;

public class ProcessHandler extends LogicHandler {
	
	private ProcessFlowHandler lastFlowHandler = new RetryFlowHandler(new SuccessFlowHandler(new OutputFlowHandler(new CleanupFlowHandler())));
	private ProcessFlowHandler nextFlowHandler = new EmptyCandidateFlowHandler(new ForcedFlowHandler(new DisabledFlowHandler(new VerifyFlowHandler(new JoinFlowHandler(new ForkFlowHandler(new GroupFlowHandler(new BranchFlowHandler())))))));
	private ProcessFlowHandler errorFlowHandler = new ErrorFlowHandler(new CompensateFlowHandler());
	
	
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
				System.out.println("context finished " + context.getId());
				context.setStatus(Status.FINISHED);
				ServiceLocator.getInstance().getContextService().set(context);
			} else if (flowResult.getSelectedLogic().isPresent()) {
				SingleLogicDefinition logic = (SingleLogicDefinition) flowResult.getSelectedLogic().get();
				
				context.setLogicId(logic.getLogicId());
				context.setLogicVersion(logic.getLogicVersion());
				context.setLogicPosition(logic.getPosition());
				context.setApplication(logic.getApplication());
				
				final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
				Map<String, Object> inputs = new HashMap<>();
				logic.getInputMap().entrySet().stream().forEach(e -> {
					if (e.getValue() instanceof ExpressionLogicDefinition) {
						inputs.put(e.getKey(), evaluator.evaluate((ExpressionLogicDefinition) e.getValue(), Map.of()));
					} else {
						inputs.put(e.getKey(), e.getValue());
					}
				});
				context.setInput(inputs);
				
//				context.setLogicStepCounter(context.getLogicStepCounter() + 1);
//				context.setSubmittedAt(OffsetDateTime.now());
				context.setOutput(null);
				if (context.getStatus() == Status.INITIAL) {
					context.setStatus(Status.STARTED);
				}
//				context.setLogicExecutionTime(0);
//				context.setLogicTotalExecutionTime(0);
//				context.setExecutedAt(null);
				
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
	}
	
	private HandlerResult getNextLogic(Context context) {
		ProcessDefinition process = findProcess(context.getProcessId(), context.getProcessVersion());
		
		HandlerResult result = handleLastLogic(context, process);
		while (result.getSelectedLogic().isEmpty()) {
			if (result.getNextCandidatePosition().get().equals(context.getEndPosition())) {
				return closeContext(context);
			}
			
			Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			
			if (nextLogic.isPresent() && context.getError() != null) {
				result = errorFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
				nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
			}
			
			result = nextFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
			if (result.getNextCandidatePosition().isEmpty()) {
				return result;
			}
		}
		
		if (!context.getCompensables().isEmpty()) {
			return getNextLogic(context);
		}
		
		return HandlerResult.noCandidate();
	}
	
	private HandlerResult closeContext(Context context) {
		context.setStatus(context.getError() == null ? Status.FINISHED : Status.FAILED);
		ServiceLocator.getInstance().getContextService().set(context);
		List<TriggerService.Trigger> triggers = ServiceLocator.getInstance().getTriggerService().get(context.getId());
		System.out.println("finished context " + context.getId() + " > " + triggers.size());
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
		} else {
			result = HandlerResult.nextCandidate(context.getLogicPosition());
			while (result.getNextCandidatePosition().isPresent()) {
				Optional<BaseLogicDefinition> nextLogic = process.getLogicByPosition(result.getNextCandidatePosition().get());
				result = lastFlowHandler.handle(nextLogic, result.getNextCandidatePosition().get(), context);
				if (result.getSelectedLogic().isPresent()) {
					break;
				} 
			}
			
			if (result.getSelectedLogic().isPresent()) {
				return result;
			} else if (context.getError() != null) {
				result = HandlerResult.nextCandidate(context.getLogicPosition());
			} else {
				result = HandlerResult.nextCandidate(ProcessFlowHandler.adjacentLogicPosition(context.getLogicPosition()));
			}
		}
		
		return result;
	}
}
