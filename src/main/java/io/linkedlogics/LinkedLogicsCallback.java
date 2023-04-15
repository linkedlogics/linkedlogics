package io.linkedlogics;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextError;

public interface LinkedLogicsCallback {
	void onSuccess(Context context);
	
	void onFailure(Context context, ContextError error);
	
	void onTimeout();
}
