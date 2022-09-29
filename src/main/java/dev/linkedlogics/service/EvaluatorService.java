package dev.linkedlogics.service;

import java.util.Map;

public interface EvaluatorService extends LinkedLogicsService {

	Object evaluate(String expression, Map<String, Object> params);
}
