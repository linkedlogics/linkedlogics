package dev.linkedlogics.config;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class LinkedLogicsConfiguration {
	private static final String CONFIG_FILE = "linkedlogics.yaml";
	static Map<String, Object> configuration;
	
	static {
		try {
			configuration = load(CONFIG_FILE);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	static Map<String, Object> load(String resource) throws Exception {
		InputStream input = LinkedLogicsConfiguration.class.getClassLoader().getResourceAsStream(resource);
		if (input == null) {
			throw new IllegalArgumentException("missing " + resource);
		}
		return load(input);
	}
	
	static Map<String, Object> load(InputStream input) throws Exception {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		mapper.findAndRegisterModules();
		return mapper.readValue(input, Map.class);
	}
	
	public static boolean containsConfig(String key) {
		return getConfig(key).isPresent();
	}
	
	public static Optional<Object> getConfig(String key) {
		return Optional.ofNullable(findConfig(key, configuration));
	}
	
	public static Object getConfigOrDefault(String key, Object defaultValue) {
		return getConfig(key).orElse(defaultValue);
	}
	
	static Object findConfig(String key, Map<String, Object> configuration) {
		String[] keys = key.split("\\.");
		Map<String, Object> cursor = configuration;
		for (int i = 0; i < keys.length - 1; i++) {
			cursor = (Map<String, Object>) cursor.get(keys[i]);
			if (cursor == null) {
				return null;
			}
		}
		
		return cursor.get(keys[keys.length - 1]);
	}
}
