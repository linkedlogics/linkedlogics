package dev.linkedlogics.context;

import java.util.HashMap;
import java.util.Map;

import dev.linkedlogics.service.LogicService;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Context {
	private String id;
	private String key;
	private String parentId;
	
	private String processId;
	private int processVersion;
	private Map<String, Object> params = new HashMap<>();
	private String startPosition = "0";
	private String endPosition;
	
	private String logicId;
	private int logicVersion = LogicService.LATEST_VERSION;
	private String logicPositionId;
	private String application;
	private Map<String, Object> input = new HashMap<>();
	private Map<String, Object> output = new HashMap<>();
	private ContextError error;
}
