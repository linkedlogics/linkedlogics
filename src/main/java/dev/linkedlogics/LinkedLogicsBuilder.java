package dev.linkedlogics;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
import dev.linkedlogics.model.process.ScriptLogicDefinition.ScriptLogicBuilder;
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
	
	public static ErrorLogicDefinition.ErrorLogicBuilder handle(ErrorLogicBuilder error) {
		return error;
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error() {
		return new ErrorLogicBuilder();
	}
	
	public static ErrorLogicDefinition.ErrorLogicBuilder error(BaseLogicDefinition errorLogic) {
		return new ErrorLogicBuilder().usingLogic(errorLogic);
	}
	
	public static RetryLogicDefinition.RetryLogicBuilder retry(int maxRetries, int delay) {
		return new RetryLogicDefinition.RetryLogicBuilder(maxRetries, delay);
	}
	
	public static ExpressionLogicDefinition expr(String expression) {
		return new ExpressionLogicDefinition.ExpressionLogicDefinitionBuilder(expression).build();
	}
	
	public static ExpressionLogicDefinition when(String expression) {
		return expr(expression);
	}
	
	public static ExpressionLogicDefinition fromText(String expression) {
		return expr(expression);
	}
	
	public static ExpressionLogicDefinition fromFile(String file) {
		try {
			return expr(new String(Files.readAllBytes(Paths.get(LinkedLogics.class.getClassLoader().getResource(file).toURI()))));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static ExpressionLogicDefinition var(String expression) {
		return expr(expression);
	}
	
	public static Integer max(Integer i) {
		return i;
	}
	
	public static Integer seconds(Integer s) {
		return s;
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
	
	public static ScriptLogicBuilder script(ExpressionLogicDefinition expression) {
		return new ScriptLogicBuilder(expression);
	}
}
