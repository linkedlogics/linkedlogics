package io.linkedlogics.test;

import java.util.Arrays;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class LinkedLogicsExtension implements BeforeEachCallback, AfterEachCallback {

	@Override
	public void afterEach(ExtensionContext context) throws Exception {
		LinkedLogics.shutdown();
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		LinkedLogics.configure(new LocalServiceConfigurer().configure(new TestContextService()));
		LinkedLogicsRegister annotation = context.getRequiredTestInstance().getClass().getAnnotation(LinkedLogicsRegister.class);
		
		if (annotation != null) {
			Arrays.stream(annotation.classes())
				  .forEach(c -> LinkedLogics.registerLogic(getInstance(c)));
			
			Arrays.stream(annotation.staticClasses())
			  .forEach(c -> LinkedLogics.registerLogic(c));
			
			Arrays.stream(annotation.classes())
			  .forEach(c -> LinkedLogics.registerProcess(getInstance(c)));
		
			Arrays.stream(annotation.staticClasses())
			  .forEach(c -> LinkedLogics.registerProcess(c));
		} else {
			LinkedLogics.registerLogic(context.getRequiredTestInstance());
			LinkedLogics.registerProcess(context.getRequiredTestInstance());
		}
		
		LinkedLogics.launch();
	}
	
	private Object getInstance(Class c) {
		try {
			return c.getConstructor().newInstance();
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
}
