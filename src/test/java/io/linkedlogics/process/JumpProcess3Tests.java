package io.linkedlogics.process;

import static io.linkedlogics.LinkedLogicsBuilder.createProcess;
import static io.linkedlogics.LinkedLogicsBuilder.expr;
import static io.linkedlogics.LinkedLogicsBuilder.fromText;
import static io.linkedlogics.LinkedLogicsBuilder.jump;
import static io.linkedlogics.LinkedLogicsBuilder.logic;
import static io.linkedlogics.LinkedLogicsBuilder.script;
import static io.linkedlogics.process.helper.ProcessTestHelper.waitUntil;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.linkedlogics.LinkedLogics;
import io.linkedlogics.annotation.Input;
import io.linkedlogics.annotation.Logic;
import io.linkedlogics.context.Context;
import io.linkedlogics.context.Status;
import io.linkedlogics.model.ProcessDefinition;
import io.linkedlogics.model.process.BaseLogicDefinition;
import io.linkedlogics.model.process.BranchLogicDefinition;
import io.linkedlogics.model.process.GroupLogicDefinition;
import io.linkedlogics.model.process.ProcessLogicDefinition;
import io.linkedlogics.model.process.VerifyLogicDefinition;
import io.linkedlogics.model.process.helper.LogicFinder;
import io.linkedlogics.service.ContextService;
import io.linkedlogics.service.ServiceLocator;
import io.linkedlogics.service.handler.process.ProcessFlowHandler;
import io.linkedlogics.service.local.LocalServiceConfigurer;

public class JumpProcess3Tests {

	@ParameterizedTest
	@MethodSource("provideFinderForProcessA")
	public void shouldComparePositions(String position1, String position2, boolean comparisonResult) {
		assertThat(ProcessFlowHandler.compareLogicPosition(position1, position2)).isEqualTo(comparisonResult);
	}
	
	private static Stream<Arguments> provideFinderForProcessA() {
		return Stream.of(
			Arguments.of("1", "2", true),
			Arguments.of("2", "1", false),
			Arguments.of("1.1.1.1", "2", true),
			Arguments.of("1", "2.1.1.2", true),
			Arguments.of("1.1.1.1.3", "1.1.1.1.2", false),
			Arguments.of("1.1.1.1.3", "1.1.1.1", true)
		);
	}
}
