package dev.linkedlogics.context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
	private Status status = Status.INITIAL;
	
	private String processId;
	private int processVersion;
	private Map<String, Object> params = new HashMap<>();
	private String startPosition = "0";
	private String endPosition;
	
	private String logicId;
	private int logicVersion = LogicService.LATEST_VERSION;
	private String logicPosition;
	private String application;
	private Map<String, Object> input = new HashMap<>();
	private Map<String, Object> output = new HashMap<>();
	private ContextError error;
	
	private Map<String, Integer> retries = new HashMap<>();
	private List<String> compensables = new ArrayList<>();
	private Map<String, String> joinMap = new HashMap<>();
}