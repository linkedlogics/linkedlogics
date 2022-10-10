package dev.linkedlogics;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.ContextError;

public interface LinkedLogicsCallback {
	void onSuccess(Context context);
	
	void onFailure(Context context, ContextError error);
	
	void onTimeout();
}
