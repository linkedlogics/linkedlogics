package dev.linkedlogics;

import dev.linkedlogics.service.ServiceConfigurer;
import dev.linkedlogics.service.ServiceLocator;

public class LinkedLogics {
	public static void configure(ServiceConfigurer configurer) {
		ServiceLocator.getInstance().configure(configurer);
	}
	
	public static void registerLogics(Object logics) {
		ServiceLocator.getInstance().getLogicService().register(logics);
	}
}
