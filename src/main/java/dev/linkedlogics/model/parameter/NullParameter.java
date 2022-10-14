package dev.linkedlogics.model.parameter;

import dev.linkedlogics.context.LogicContext;

public class NullParameter extends Parameter {

	public NullParameter() {
		super(null, null, false, false);
	}
	
	@Override
	protected Object getValue(LogicContext context) {
		return null;
	}
}
