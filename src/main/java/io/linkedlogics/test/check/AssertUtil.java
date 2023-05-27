package io.linkedlogics.test.check;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import io.linkedlogics.context.Context;
import io.linkedlogics.context.ContextFlow.Type;

public class AssertUtil {

	public static Boolean getResult(Context context, Type type, String id) {
		return getMapByType(context, type).get(id);
	}
	
	public static Boolean getResultOrDefault(Context context, Type type, String id, Boolean defaultResult) {
		return getMapByType(context, type).getOrDefault(id, defaultResult);
	}
	
	public static Integer countResults(Context context, Type type, String id, Boolean result) {
		return getListByType(context, type, result).stream().filter(s -> s.equals(id)).collect(Collectors.toList()).size();
	}
	
	public static Map<String, Boolean> getMapByType(Context context, Type type) {
		return context.getExecList()
			.stream()
			.map(s -> s.split("\\|"))
			.filter(s -> s[0].equals(type.name()))
			.collect(Collectors.toMap(s -> (String) s[1], s -> Boolean.parseBoolean(s[2]), (a, b) -> b));
	}
	
	public static Map<String, Boolean> getMap(Context context) {
		return context.getExecList()
			.stream()
			.map(s -> s.split("\\|"))
			.collect(Collectors.toMap(s -> (String) s[1], s -> Boolean.parseBoolean(s[2]), (a, b) -> b));
	}
	
	public static Set<String> getSet(Context context) {
		return context.getExecList()
			.stream()
			.map(s -> s.split("\\|")[1])
			.collect(Collectors.toSet());
	}
	
	public static Set<String> getSetByType(Context context, Type type) {
		return context.getExecList()
			.stream()
			.map(s -> s.split("\\|"))
			.filter(s -> s[0].equals(type.name()))
			.map(s -> s[1])
			.collect(Collectors.toSet());
	}
	
	public static List<String> getListByType(Context context, Type type, Boolean result) {
		return context.getExecList()
			.stream()
			.map(s -> s.split("\\|"))
			.filter(s -> s[0].equals(type.name()))
			.filter(s -> Boolean.parseBoolean(s[2]) == result)
			.map(s -> s[1])
			.collect(Collectors.toList());
	}
}
