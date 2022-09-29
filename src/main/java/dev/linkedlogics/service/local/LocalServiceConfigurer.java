package dev.linkedlogics.service.local;

import dev.linkedlogics.service.ServiceConfigurer;

public class LocalServiceConfigurer extends ServiceConfigurer {
	public LocalServiceConfigurer() {
		configure(new LocalLogicService());
	}
}
