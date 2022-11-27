package dev.linkedlogics.service.handler.process;

import java.time.OffsetDateTime;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;

public class TimeoutFlowHandler extends ProcessFlowHandler {
	public TimeoutFlowHandler() {

	}
	
	public TimeoutFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && 
				candidate.get().getTimeout() != null && 
					candidate.get().getTimeout().getDelay() > 0) {
			OffsetDateTime expiresAt = OffsetDateTime.now().plusSeconds(candidate.get().getTimeout().getDelay());
			context.setExpiresAt(expiresAt);
		}
		
		return super.handle(candidate, candidatePosition, context);
	}
}
