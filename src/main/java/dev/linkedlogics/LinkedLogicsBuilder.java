package dev.linkedlogics;

import java.util.Set;

import dev.linkedlogics.model.ProcessDefinition.ProcessBuilder;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.BranchLogicDefinition.BranchLogicBuilder;
import dev.linkedlogics.model.process.ErrorLogicDefinition;
import dev.linkedlogics.model.process.ErrorLogicDefinition.ErrorLogicBuilder;
import dev.linkedlogics.model.process.ExitLogicDefinition.ExitLogicBuilder;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.model.process.FailLogicDefinition.FailLogicBuilder;
import dev.linkedlogics.model.process.GroupLogicDefinition.GroupLogicBuilder;
import dev.linkedlogics.model.process.JumpLogicDefinition.JumpLogicBuilder;
import dev.linkedlogics.model.process.ProcessLogicDefinition.ProcessLogicBuilder;
import dev.linkedlogics.model.process.RetryLogicDefinition;
import dev.linkedlogics.model.process.SavepointLogicDefinition.SavepointLogicBuilder;
import dev.linkedlogics.model.process.SingleLogicDefinition.SingleLogicBuilder;
import dev.linkedlogics.model.process.VerifyLogicDefinition.VerifyLogicBuilder;
import dev.linkedlogics.service.LogicService;
import dev.linkedlogics.service.ProcessService;

public class LinkedLogicsBuilder {
	
	public static ProcessBuilder createProcess(String id, int version) {
		return new ProcessBuilder().id(id).version(version);
	}
	
	public static ProcessBuilder createProcess(String id) {
		return new ProcessBuilder().id(id).version(ProcessService.LATEST_VERSION);
	}
	
	public static SingleLogicBuilder logic(String id, int version) {
		return new SingleLogicBuilder().id(id).version(version);
	}
	
	public static SingleLogicBuilder logic(String id) {
		return new SingleLogicBuilder().id(id).version(LogicService.LATEST_VERSION);
	}
	
	public static GroupLogicBuilder group(BaseLogicDefinition... logics) {
		return new GroupLogicBuilder(logics);
	}
	
	public static ProcessLogicBuilder process(String processId, int version) {
		return new ProcessLogicBuilder(processId, version);
	}
	
	public static BranchLogicBuilder branch(ExpressionLogicDefinition expression, BaseLogicDefinition leftBranch, BaseLogicDefinition rightBranch) {
		return new BranchLogicBuilder(expression, leftBranch, rightBranch);
	}
	
	public static BranchLogicBuilder branch(ExpressionLogicDefinition expression, BaseLogicDefinition leftBranch) {
		return new BranchLogicBuilder(expression, leftBranch, null);
	}
	
	public static VerifyLogicBuilder verify(ExpressionLogicDefinition expression) {
		return new VerifyLogicBuilder(expression);
	}
	
	public static SavepointLogicBuilder savepoint() {
		return new SavepointLogicBuilder();
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error(Integer... errorCodes) {
		return new ErrorLogicBuilder().errorCodeSet(errorCodes);
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error(String... errorMessages) {
		return new ErrorLogicBuilder().errorMessageSet(errorMessages);
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error() {
		return new ErrorLogicBuilder();
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error(BaseLogicDefinition errorLogic) {
		return new ErrorLogicBuilder().errorLogic(errorLogic);
	}
	
	public static RetryLogicDefinition.RetryLogicBuilder retry(int maxRetries, int delay) {
		return new RetryLogicDefinition.RetryLogicBuilder(maxRetries, delay);
	}
	
	public static ExpressionLogicDefinition expr(String expression) {
		return new ExpressionLogicDefinition.ExpressionLogicDefinitionBuilder(expression).build();
	}
	
	public static JumpLogicBuilder jump(String label) {
		return new JumpLogicBuilder(label);
	}
	
	public static ExitLogicBuilder exit() {
		return new ExitLogicBuilder();
	}
	
	public static FailLogicBuilder fail() {
		return new FailLogicBuilder();
	}
}
