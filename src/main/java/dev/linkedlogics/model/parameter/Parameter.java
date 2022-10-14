package dev.linkedlogics.model.parameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Optional;

import dev.linkedlogics.annotation.Input;
import dev.linkedlogics.context.LogicContext;
import dev.linkedlogics.exception.MissingInputParameterError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class Parameter {
	protected String name;
	protected Class<?> type;
	protected boolean required;
	protected boolean returned;

	public Object getParameterValue(LogicContext context) {
		Object value = getValue(context);
		if (value == null && isRequired()) {
			throw new MissingInputParameterError(getName());
		}
		
		if (getType() != null && Optional.class.isAssignableFrom(getType())) {
			return Optional.ofNullable(value);
		}
		
		return value;
	}

	protected abstract Object getValue(LogicContext context);

	public static Parameter[] initParameters(Method method) {
		Parameter[] parameters = new Parameter[method.getParameterCount()];

		for (int i = 0; i < method.getParameterCount(); i++) {
			parameters[i] = initParameter(method.getParameters()[i].getType(), method.getParameters()[i].getAnnotations());
		}

		return parameters;
	}
	
	public static Parameter initParameter(Class<?> type, Annotation[] annotations) {
		if (annotations.length == 0) {
			return new NullParameter();
		} else {
			for (Annotation annotation : annotations) {
				if (annotation instanceof Input) {
					Input param = (Input) annotation ;
					return new InputParameter(param.value(), type, param.required(), param.returned());
				}
			}
			return new NullParameter();
		}
	}
}
