package dev.linkedlogics.service.handler.process;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.Optional;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.context.Status;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.TriggerService;

public class JoinFlowHandler extends ProcessFlowHandler {
	public JoinFlowHandler() {

	}

	public JoinFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getJoin() != null) {
			Optional<String> missingContextId = Arrays.stream(candidate.get().getJoin().getKey())
					.map(k -> context.getJoinMap().get(k))
					.map(k -> ServiceLocator.getInstance().getContextService().get(k))
					.filter(c -> c.get().getStatus() == Status.STARTED || c.get().getStatus() == Status.INITIAL)
					.map(c -> c.get().getId())
					.findAny();
			
			if (missingContextId.isPresent()) {
				ServiceLocator.getInstance().getTriggerService().set(missingContextId.get(), new TriggerService.Trigger(context.getId(), candidatePosition));
				context.setStatus(Status.WAITING);
				return HandlerResult.noCandidate();
			} else {
				context.setStatus(Status.STARTED);
				return super.handle(candidate, candidatePosition, context);
			}
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
}
