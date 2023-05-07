package io.linkedlogics.config;

import java.util.Optional;

public class LinkedLogicsConfiguration {
	public static final String LINKEDLOGICS = "linkedlogics";
	private static final String DEFAULT_CONFIG_FILE = "linkedlogics.yaml";
	static YamlConfig config;

	static {
		try {
			String configFile = System.getProperty("linkedlogics.config", DEFAULT_CONFIG_FILE);
			config = new YamlConfig(configFile);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Optional<Object> get(String key) {
		return Optional.ofNullable(config.getNested(key));
	}

	public static Object getOrDefault(String key, Object defaultValue) {
		return Optional.ofNullable(config.getNested(key)).orElse(defaultValue);
	}
	
	public static Object getOrThrow(String key, String message) {
		return Optional.ofNullable(config.getNested(key)).orElseThrow(() -> new NullPointerException(message));
	}
}
