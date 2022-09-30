package dev.linkedlogics;

import dev.linkedlogics.service.ServiceConfigurer;
import dev.linkedlogics.service.ServiceLocator;

public class LinkedLogics {
	public static void configure(ServiceConfigurer configurer) {
		ServiceLocator.getInstance().configure(configurer);
	}
	
	public static void registerLogic(Object logic) {
		ServiceLocator.getInstance().getLogicService().register(logic);
	}
	
	public static void registerProcess(Object process) {
		ServiceLocator.getInstance().getProcessService().register(process);
	}
}
