package dev.linkedlogics.service;

import java.util.Map;

import dev.linkedlogics.model.process.ExpressionLogicDefinition;

public interface EvaluatorService extends LinkedLogicsService {

	Object evaluate(ExpressionLogicDefinition expression, Map<String, Object> params);
}
