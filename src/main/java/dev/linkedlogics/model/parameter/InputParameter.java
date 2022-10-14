package dev.linkedlogics.model.parameter;

import dev.linkedlogics.context.LogicContext;

public class InputParameter extends Parameter {

	public InputParameter(String name, Class<?> type, boolean required, boolean returned) {
		super(name, type, required, returned);
	}
	
	@Override
	protected Object getValue(LogicContext context) {
		return context.getInput().get(name);
	}
}
