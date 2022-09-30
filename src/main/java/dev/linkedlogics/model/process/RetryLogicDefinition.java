package dev.linkedlogics.model.process;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RetryLogicDefinition {
	private int maxRetries;
	private int delay;
}
