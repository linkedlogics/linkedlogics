package dev.linkedlogics.context;

import java.util.HashMap;
import java.util.Map;

import dev.linkedlogics.service.LogicService;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LogicContext {
	private String id;
	
	private String logicId;
	private int logicVersion = LogicService.LATEST_VERSION;
	private String position;
	private String application;

	private Map<String, Object> input = new HashMap<>();
	private Map<String, Object> output = new HashMap<>();

	private ContextError error;
}
