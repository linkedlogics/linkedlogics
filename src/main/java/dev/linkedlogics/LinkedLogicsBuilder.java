package dev.linkedlogics;

import java.util.List;
import java.util.Set;

import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.BranchLogicDefinition.BranchLogicBuilder;
import dev.linkedlogics.model.process.ErrorLogicDefinition;
import dev.linkedlogics.model.process.ErrorLogicDefinition.ErrorLogicDefinitionBuilder;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.model.process.GroupLogicDefinition.GroupLogicBuilder;
import dev.linkedlogics.model.process.ProcessDefinition;
import dev.linkedlogics.model.process.ProcessDefinition.ProcessBuilder;
import dev.linkedlogics.model.process.SavepointLogicDefinition.SavepointLogicBuilder;
import dev.linkedlogics.model.process.SingleLogicDefinition.SingleLogicBuilder;
import dev.linkedlogics.model.process.VerifyLogicDefinition.VerifyLogicBuilder;
import dev.linkedlogics.service.LogicService;
import dev.linkedlogics.service.ProcessService;
import dev.linkedlogics.service.ServiceLocator;

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
	
	public static GroupLogicBuilder process(String processId, int version) {
		return ServiceLocator.getInstance().getProcessService().getProcess(processId, version).map(w -> {
			List<BaseLogicDefinition> logics = w.cloneLogics();
			BaseLogicDefinition[] logicArray = new BaseLogicDefinition[logics.size()];
			logics.toArray(logicArray);
			
			return new GroupLogicBuilder(logicArray);
		}).orElseThrow(() -> new IllegalArgumentException("missing process " + processId));
	}
	
	public static GroupLogicBuilder process(ProcessDefinition process) {
		List<BaseLogicDefinition> logics = process.cloneLogics();
		BaseLogicDefinition[] logicArray = new BaseLogicDefinition[logics.size()];
		logics.toArray(logicArray);
		
		return new GroupLogicBuilder(logicArray).inputs(process.getInputs());
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
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error(Integer... errorCodes) {
		return new ErrorLogicDefinitionBuilder().errorCodeSet(Set.of(errorCodes));
	}
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error(String... errorMessages) {
		return new ErrorLogicDefinitionBuilder().errorMessageSet(Set.of(errorMessages));
	}
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error() {
		return new ErrorLogicDefinitionBuilder();
	}
	
	public static ErrorLogicDefinition.ErrorLogicDefinitionBuilder error(BaseLogicDefinition errorLogic) {
		return new ErrorLogicDefinitionBuilder().errorLogic(errorLogic);
	}
	
	public static ExpressionLogicDefinition expr(String expression) {
		return new ExpressionLogicDefinition.ExpressionLogicDefinitionBuilder(expression).build();
	}
}
