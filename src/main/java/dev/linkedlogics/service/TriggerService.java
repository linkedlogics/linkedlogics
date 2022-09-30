package dev.linkedlogics.service;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

public interface TriggerService extends LinkedLogicsService {
	void set(String contextId, Trigger trigger);
	
	List<Trigger> get(String contextId);
	
	@Getter
	@AllArgsConstructor
	public static class Trigger {
		private String contextId;
		private String position;
	}
}
