package dev.linkedlogics.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.linkedlogics.annotation.Logic;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter(value = AccessLevel.PACKAGE)
public class ProcessDefinition {
	public static final int LATEST_VERSION = -1;
	
	private String id;
	private int version = LATEST_VERSION;
	private Map<String, Object> inputMap = new HashMap<>();
	
	@Getter(value = AccessLevel.PACKAGE)
	private List<Logic> logics = new ArrayList<Logic>();
	@Getter(value = AccessLevel.PUBLIC)
	private Map<String, Logic> positionMap;
}

	