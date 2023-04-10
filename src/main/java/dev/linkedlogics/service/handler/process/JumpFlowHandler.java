package dev.linkedlogics.service.handler.process;

import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;
import dev.linkedlogics.context.ContextError.ErrorType;
import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.JumpLogicDefinition;
import dev.linkedlogics.service.EvaluatorService;
import dev.linkedlogics.service.ProcessService;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

public class JumpFlowHandler extends ProcessFlowHandler {
	private ProcessService processService;
	private EvaluatorService evaluatorService;
	
	public JumpFlowHandler() {
		processService = ServiceLocator.getInstance().getProcessService();
		evaluatorService = ServiceLocator.getInstance().getEvaluatorService();
	}

	public JumpFlowHandler(ProcessFlowHandler handler) {
		super(handler);
		processService = ServiceLocator.getInstance().getProcessService();
		evaluatorService = ServiceLocator.getInstance().getEvaluatorService();
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()) {
			if (candidate.get() instanceof JumpLogicDefinition) {
				JumpLogicDefinition jump = (JumpLogicDefinition) candidate.get();
				log(context, "jumping", candidatePosition, Flow.RESET);
				
				String targetLabel = null;
				if (jump.getTargetLabel() != null) {
					targetLabel = jump.getTargetLabel();
				} else if (jump.getTargetExpr() != null) {
					targetLabel = (String) evaluatorService.evaluate(jump.getTargetExpr().getExpression(), context.getParams());
				}
				
				ProcessDefinition process = processService.getProcess(context.getProcessId(), context.getProcessVersion()).get();
				BaseLogicDefinition target =process.getLabels().get(targetLabel);
				
				if (target == null) {
					context.setError(new ContextError(-1, "jum failed target " + targetLabel + " not found", ErrorType.PERMANENT));
					log(context, "not verified", candidatePosition, Flow.RESET);
					return HandlerResult.nextCandidate(candidatePosition);
				}
				
				return HandlerResult.nextCandidate(target.getPosition());
			}
			log(context, "no jump", candidatePosition, Flow.CONTINUE);
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}