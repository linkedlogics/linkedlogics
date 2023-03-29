package dev.linkedlogics.service;

import java.util.List;
import java.util.stream.Collectors;

public interface ServiceProvider {
	default List<LinkedLogicsService> getStoringServices() {
		return List.of();
	}
	
	default List<LinkedLogicsService> getMessagingServices() {
		return List.of();
	}
	
	default List<LinkedLogicsService> getSchedulingServices() {
		return List.of();
	}
	
	default List<LinkedLogicsService> getProcessingServices() {
		return List.of();
	}
	
	default List<LinkedLogicsService> getMonitoringServices() {
		return List.of();
	}
	
	default List<LinkedLogicsService> getEvaluatingServices() {
		return List.of();
	}
	
	default List<LinkedLogicsService> getServices() {
		return List.of(getStoringServices(),
						getMessagingServices(),
						getSchedulingServices(),
						getProcessingServices(),
						getMonitoringServices(),
						getEvaluatingServices())
				.stream()
				.flatMap(l -> l.stream())
				.collect(Collectors.toList());
	}
}
