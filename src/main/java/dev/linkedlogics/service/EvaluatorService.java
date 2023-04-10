package dev.linkedlogics.service;

import java.util.Map;

public interface EvaluatorService extends LinkedLogicsService {

	Object evaluate(String expression, Map<String, Object> params);
	
	default Object evaluateScript(String expression, String id, Map<String, Object> params) {
		return evaluate(expression, params);
	}
}
