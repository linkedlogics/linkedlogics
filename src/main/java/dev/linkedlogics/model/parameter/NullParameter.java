package dev.linkedlogics.model.parameter;

import dev.linkedlogics.context.Context;

public class NullParameter extends Parameter {

	public NullParameter() {
		super(null, null, false, false);
	}
	
	@Override
	protected Object getValue(Context context) {
		return null;
	}
}
