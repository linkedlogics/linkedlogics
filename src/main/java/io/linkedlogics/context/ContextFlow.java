package io.linkedlogics.context;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ContextFlow {
	private String type;
	private String position;
	private String name;
	private String result;
	
	private String format = "FLOW %-12s %s %s %s";
	
	private ContextFlow(String type) {
		this.type = type;
	}
	
	public static ContextFlow start() {
		return new ContextFlow("START");
	}
	
	public static ContextFlow finish() {
		return new ContextFlow("FINISH");
	}
	
	public static ContextFlow logic() {
		return new ContextFlow("LOGIC");
	}
	
	public static ContextFlow script() {
		return new ContextFlow("SCRIPT");
	}
	
	public static ContextFlow group() {
		return new ContextFlow("GROUP");
	}
	
	public static ContextFlow branch() {
		return new ContextFlow("BRANCH");
	}
	
	public static ContextFlow verify() {
		return new ContextFlow("VERIFY");
	}
	
	public static ContextFlow delay() {
		return new ContextFlow("DELAY");
	}
	
	public static ContextFlow retry() {
		return new ContextFlow("RETRY");
	}
	
	public static ContextFlow log() {
		return new ContextFlow("LOG");
	}
	
	public ContextFlow position(String position) {
		this.position = position;
		return this;
	}
	
	public ContextFlow name(String name) {
		this.name = name;
		return this;
	}
	
	public ContextFlow result(String result) {
		this.result = result;
		return this;
	}
	
	public String getLog() {
		return String.format(format, type, position, name != null ? name : "", result != null ? "-> " + result : "");
	}
	
	public void info() {
//		if (log.isInfoEnabled()) {
			log.error(getLog());
//		}
	}
}
