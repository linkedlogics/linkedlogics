package dev.linkedlogics.service.local;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import dev.linkedlogics.service.TriggerService;

public class LocalTriggerService implements TriggerService {
	private static ConcurrentHashMap<String, List<Trigger>> triggerMap = new ConcurrentHashMap<>();
	
	@Override
	public void set(String contextId, Trigger trigger) {
		if (triggerMap.contains(contextId)) {
			triggerMap.get(contextId).add(trigger);
		} else {
			triggerMap.put(contextId, new ArrayList<Trigger>() {{ add(trigger); }});
		}
	}

	@Override
	public List<Trigger> get(String contextId) {
		List<Trigger> triggers = triggerMap.remove(contextId);
		if (triggers == null) {
			return List.of();
		}
		return triggers;
	}
}