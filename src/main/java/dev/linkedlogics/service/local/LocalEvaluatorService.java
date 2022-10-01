package dev.linkedlogics.service.local;

import java.util.Map;

import org.springframework.context.expression.MapAccessor;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.service.EvaluatorService;

public class LocalEvaluatorService implements EvaluatorService {
	private ExpressionParser parser;
	
	public LocalEvaluatorService() {
		parser = new SpelExpressionParser();
	}
	
	@Override
	public Object evaluate(ExpressionLogicDefinition expression, Map<String, Object> params) {
		StandardEvaluationContext context = new StandardEvaluationContext(params);
		context.addPropertyAccessor(new MapAccessor());
	    return parser.parseExpression(expression.getExpression()).getValue(context);
	}
}
