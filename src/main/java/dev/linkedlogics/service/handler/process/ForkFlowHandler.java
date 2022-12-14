package dev.linkedlogics.service.handler.process;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.linkedlogics.context.Context;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.service.ServiceLocator;
import dev.linkedlogics.service.task.StartTask;

public class ForkFlowHandler extends ProcessFlowHandler {
	public ForkFlowHandler() {

	}
	
	public ForkFlowHandler(ProcessFlowHandler handler) {
		super(handler);
	}

	@Override
	public HandlerResult handle(Optional<BaseLogicDefinition> candidate, String candidatePosition, Context context) {
		if (candidate.isPresent() && candidate.get().getFork() != null) {
			
			if (ifNotAlreadyForked(candidatePosition, context)) {
				Context forkContext = forkContext(context, candidatePosition);
				ServiceLocator.getInstance().getContextService().set(forkContext);
				
				if (candidate.get().getFork().getKey().isPresent()) {
					context.getJoinMap().put(candidate.get().getFork().getKey().get(), forkContext.getId());
				}
				
				ServiceLocator.getInstance().getProcessorService().process(new StartTask(forkContext));
				return HandlerResult.nextCandidate(adjacentLogicPosition(candidatePosition));
			}
			return super.handle(candidate, candidatePosition, context);
		} else {
			return super.handle(candidate, candidatePosition, context);
		}
	}
	
	private boolean ifNotAlreadyForked(String candidatePosition, Context context) {
		return !candidatePosition.equals(context.getStartPosition());
	}
	
	private Context forkContext(Context parent, String candidatePosition) {
		Context forked = new Context(parent.getProcessId(), parent.getProcessVersion(), cloneParams(parent.getParams()));
		forked.setParentId(parent.getId());
		forked.setId(UUID.randomUUID().toString());
		forked.setKey(parent.getKey());
		forked.setStartPosition(candidatePosition);
		forked.setEndPosition(adjacentLogicPosition(candidatePosition));
		forked.setLogicId(null);
		forked.setApplication(parent.getApplication());
		return forked;
	}
	
	private Map<String, Object> cloneParams(Map<String, Object> params) {
		try {
			ObjectMapper mapper = ServiceLocator.getInstance().getMapperService().getMapper();
			return (Map<String, Object>) mapper.readValue(mapper.writeValueAsString(params), Map.class);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
