package dev.linkedlogics.model.parameter;

import dev.linkedlogics.context.Context;

public class InputParameter extends Parameter {

	public InputParameter(String name, Class<?> type, boolean required, boolean returned) {
		super(name, type, required, returned);
	}
	
	@Override
	protected Object getValue(Context context) {
		return context.getInput().get(name);
	}
}
