package dev.linkedlogics.service.handler.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.linkedlogics.LinkedLogics;
import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.service.EvaluatorService;
import dev.linkedlogics.service.ServiceLocator;

public class OutputFlowHandler extends ProcessFlowHandler {
	public OutputFlowHandler() {

	}

	public OutputFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent()
				&& context.getError() == null 
					&& candidatePosition.equals(context.getLogicPosition())) {
				
			if (context.getOutput() != null) {
				context.getOutput().entrySet().forEach(e -> context.getParams().put(e.getKey(), e.getValue()));
			}
			
			if (!candidate.get().getOutputMap().isEmpty()) {
				final EvaluatorService evaluator = ServiceLocator.getInstance().getEvaluatorService();
				candidate.get().getOutputMap().entrySet().stream().forEach(e -> {
					if (e.getValue() instanceof ExpressionLogicDefinition) {
						context.getParams().put(e.getKey(), evaluator.evaluate((ExpressionLogicDefinition) e.getValue(), context.getParams()));
					} else {
						context.getParams().put(e.getKey(), e.getValue());
					}
				});
			}
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
