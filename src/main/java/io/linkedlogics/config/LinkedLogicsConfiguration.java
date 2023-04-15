package io.linkedlogics.config;

import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

import org.yaml.snakeyaml.Yaml;

public class LinkedLogicsConfiguration {
	public static final String LINKEDLOGICS = "linkedlogics";
	public static final String APPLICATION_NAME = "application.name";
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
		Yaml yaml = new Yaml();
		return yaml.load(input);
	}

	public static boolean containsConfig(String key) {
		return getConfig(key).isPresent();
	}

	public static Optional<Object> getConfig(String key) {
		return getConfig(LINKEDLOGICS, key);
	}

	public static Optional<Object> getConfig(String prefix, String key) {
		return Optional.ofNullable(findConfig(String.join(".", prefix, key), configuration));
	}

	public static Object getConfigOrDefault(String key, Object defaultValue) {
		return getConfigOrDefault(LINKEDLOGICS, key, defaultValue);
	}

	public static Object getConfigOrDefault(String prefix, String key, Object defaultValue) {
		return getConfig(prefix, key).orElse(defaultValue);
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
