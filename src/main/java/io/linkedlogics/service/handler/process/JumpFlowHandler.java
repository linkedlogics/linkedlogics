package io.linkedlogics.service.handler.process;

import java.util.Optional;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;
import io.linkedlogics.context.ContextError.ErrorType;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.JumpLogicDefinition;
import io.linkedlogics.service.EvaluatorService;
import io.linkedlogics.service.ProcessService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.handler.process.ProcessFlowHandler.Flow;

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