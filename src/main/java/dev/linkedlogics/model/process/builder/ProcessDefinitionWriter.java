package dev.linkedlogics.model.process.builder;

import static dev.linkedlogics.LinkedLogicsBuilder.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

import dev.linkedlogics.model.ProcessDefinition;
import dev.linkedlogics.model.process.BaseLogicDefinition;
import dev.linkedlogics.model.process.BranchLogicDefinition;
import dev.linkedlogics.model.process.DelayLogicDefinition;
import dev.linkedlogics.model.process.ErrorLogicDefinition;
import dev.linkedlogics.model.process.ExitLogicDefinition;
import dev.linkedlogics.model.process.ExpressionLogicDefinition;
import dev.linkedlogics.model.process.FailLogicDefinition;
import dev.linkedlogics.model.process.ForkLogicDefinition;
import dev.linkedlogics.model.process.GroupLogicDefinition;
import dev.linkedlogics.model.process.JoinLogicDefinition;
import dev.linkedlogics.model.process.JumpLogicDefinition;
import dev.linkedlogics.model.process.LabelLogicDefinition;
import dev.linkedlogics.model.process.RetryLogicDefinition;
import dev.linkedlogics.model.process.SavepointLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.model.process.TimeoutLogicDefinition;
import dev.linkedlogics.model.process.TypedLogicDefinition;
import dev.linkedlogics.model.process.VerifyLogicDefinition;
import dev.linkedlogics.model.process.VerifyLogicDefinition.VerifyLogicBuilder;
import dev.linkedlogics.service.LogicService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessDefinitionWriter {
	private static String TAB = "    ";
	private static String LINE = "\n";
	private static String BUILD = ".build()";
	private ProcessDefinition process;

	public String write() {
		StringBuilder script = new StringBuilder("import static dev.linkedlogics.LinkedLogicsBuilder.*;\n\n");
		script(script, process);
		return script.toString();
	}

	private void script(StringBuilder script, ProcessDefinition process) {
		script.append("return createProcess(\"").append(process.getId()).append("\", ").append(process.getVersion()).append(")\n");
		script.append(TAB);
		script(script, process.getInputs());
		
		process.getLogics().forEach(l -> {
			script.append(LINE).append(TAB).append(".add(");
			script(script, l);
			script.append(")");
		});
		
		script.append(LINE).append(BUILD).append(";");
	}
	
	private void script(StringBuilder script, BaseLogicDefinition logic) {
		if (logic instanceof SingleLogicDefinition) {
			script(script, (SingleLogicDefinition) logic);
			script(script, logic.getInputs());
			
			if (logic.getRetry() != null) {
				script(script, logic.getRetry());
			}
			
			if (logic.getError() != null) {
				script(script, logic.getError());
			}
		} else if (logic instanceof BranchLogicDefinition) {
			script(script, (BranchLogicDefinition) logic);
		} else if (logic instanceof ExitLogicDefinition) {
			script(script, (ExitLogicDefinition) logic);
		} else if (logic instanceof FailLogicDefinition) {
			script(script, (FailLogicDefinition) logic);
		} else if (logic instanceof GroupLogicDefinition) {
			script(script, (GroupLogicDefinition) logic);
		} else if (logic instanceof JumpLogicDefinition) {
			script(script, (JumpLogicDefinition) logic);
		} else if (logic instanceof SavepointLogicDefinition) {
			script(script, (SavepointLogicDefinition) logic);
		} else if (logic instanceof VerifyLogicDefinition) {
			script(script, (VerifyLogicDefinition) logic);
		} else {
			script.append("MISSING TYPE ").append(logic.getType());
		}
		
		
		if (logic.getForced()) {
			script.append(".forced()");
		}
		
		if (logic.getDelay() != null) {
			script(script, logic.getDelay());
		}
		
		
		if (logic.getFork() != null) {
			script(script, logic.getFork());
		}
		
		if (logic.getJoin() != null) {
			script(script, logic.getJoin());
		}
		
		if (logic.getDisabled()) {
			script.append(".disabled()");
		}
		
		if (logic.getLabel() != null) {
			script(script, logic.getLabel());
		}

		if (logic.getTimeout() != null) {
			script(script, logic.getTimeout());
		}
		
		script.append(BUILD);
	}
	
	private void script(StringBuilder script, RetryLogicDefinition retry) {
		script.append(".retry(retry(max(").append(retry.getMaxRetries()).append("), seconds(").append(retry.getSeconds()).append("))");
		
		if (retry.getErrorCodeSet().size() > 0) {
			if (retry.isExclude()) {
				script.append(".excludeCodes(").append(retry.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", ")));
			} else {
				script.append(".includeCodes(").append(retry.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", ")));
			}
			script.append(")");
		}
		
		if (retry.getErrorMessageSet().size() > 0) {
			script.append(".andMessages(");
			script.append(retry.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
			script.append(")");
		}
		
		script.append(BUILD).append(")");
	}
	
	private void script(StringBuilder script, DelayLogicDefinition delay) {
		script.append(".delayed(seconds(").append(delay.getSeconds()).append("))");
	}
	
	private void script(StringBuilder script, ExitLogicDefinition exit) {
		script.append("exit()");
	}
	
	private void script(StringBuilder script, JumpLogicDefinition jump) {
		script.append("jump(\"").append(jump.getTargetLabel()).append("\")");
	}
	
	private void script(StringBuilder script, SavepointLogicDefinition jump) {
		script.append("savepoint()");
	}
	
	private void script(StringBuilder script, VerifyLogicDefinition verify) {
		script.append("verify(when(\"").append(verify.getExpression().getExpression()).append("\"))");
		if (verify.getErrorCode() != null) {
			script.append(".elseFailWithCode(").append(verify.getErrorCode()).append(")");
			
			if (verify.getErrorMessage() != null) {
				script.append(".andMessage(\"").append(verify.getErrorMessage()).append("\")");
			}
		} else if (verify.getErrorMessage() != null) {
			script.append(".elseFailWithMessage(\"").append(verify.getErrorMessage()).append("\")");
		}
	}
	
	private void script(StringBuilder script, FailLogicDefinition fail) {
		script.append("fail()");
		if (fail.getErrorCode() != null) {
			script.append(".withCode(").append(fail.getErrorCode()).append(")");
			
			if (fail.getErrorMessage() != null) {
				script.append(".andMessage(\"").append(fail.getErrorMessage()).append("\")");
			}
		} else if (fail.getErrorMessage() != null) {
			script.append(".withMessage(\"").append(fail.getErrorMessage()).append("\")");
		}
	}
	
	private void script(StringBuilder script, ForkLogicDefinition fork) {
		script.append(".fork(").append(fork.getKey().map(k -> "\"" + k + "\"").orElse("")).append(")");
	}
	
	private void script(StringBuilder script, JoinLogicDefinition join) {
		script.append(".join(").append(Arrays.stream(join.getKey()).map(m -> "\"" + m + "\"").collect(Collectors.joining(", "))).append(")");
	}
	
	private void script(StringBuilder script, TimeoutLogicDefinition timeout) {
		script.append(".timeout(seconds(").append(timeout.getSeconds()).append("))");
	}
	
	private void script(StringBuilder script, LabelLogicDefinition label) {
		script.append(".label(\"").append(label.getLabel()).append("\")");
	}
	
	private void script(StringBuilder script, ErrorLogicDefinition error) {
		script.append(".handle(error()");
		
		if (!error.getErrorCodeSet().isEmpty()) {
			script.append(".withCodes(").append(error.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", "))).append(")");
		
			if (!error.getErrorMessageSet().isEmpty()) {
				script.append(".orMessages(");
				script.append(error.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
				script.append(")");
			}
		} else if (!error.getErrorMessageSet().isEmpty()) {
			script.append(".withMessages(");
			script.append(error.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
			script.append(")");
		}
		
		if (error.isThrowAgain()) {
			script.append(".throwAgain(");
			if (error.getThrowErrorCode() != null) {
				script.append(error.getThrowErrorCode()).append(", \"").append(error.getThrowErrorMessage()).append("\"");
			}
			script.append(")");
		}
		
		if (error.getErrorLogic() != null) {
			script.append(".usingLogic(");
			script(script, error.getErrorLogic());
			script.append(")");
		}
		
		script.append(BUILD).append(")");
	}
	
	private void script(StringBuilder script, BranchLogicDefinition branch) {
		script.append("branch(when(\"").append(branch.getExpression().getExpression()).append("\"), ");
		script(script, branch.getLeftLogic());
		if (branch.getRightLogic() != null) {
			script.append(", ");
			script(script, branch.getRightLogic());
		}
		script.append(")");
	}
	
	private void script(StringBuilder script, GroupLogicDefinition group) {
		script.append("group(");
		
		for (int i = 0; i < group.getLogics().size(); i++) {
			if (i > 0) {
				script.append(", ");
			}
			script(script, group.getLogics().get(i));
		}
		
		script.append(")");
	}
	
	private void script(StringBuilder script, SingleLogicDefinition logic) {
		script.append("logic(\"").append(logic.getLogicId()).append("\"");
		if (logic.getLogicVersion() != LogicService.LATEST_VERSION) {
			script.append(", ").append(logic.getLogicVersion());
		}
		script.append(")");
	}
	

	private void script(StringBuilder script, Map<String, Object> inputs) {
		script.append(".inputs(");
		script.append(inputs.entrySet().stream().map(e -> {
			StringBuilder input = new StringBuilder();
			input.append("\"").append(e.getKey()).append("\", ");
			if (e.getValue() instanceof ExpressionLogicDefinition) {
				input.append("expr(\"").append(((ExpressionLogicDefinition) e.getValue()).getExpression()).append("\")");
			} else if (e.getValue() instanceof String) { 
				input.append("\"").append(e.getValue()).append("\"");
			} else {
				input.append(e.getValue());
			}
			return input.toString();
		}).collect(Collectors.joining(", ")));
		script.append(")");
	}
	
	

	public static void main(String[] args) {
		ProcessDefinition p = createProcess("SIMPLE_SCENARIO_2", 0).input("list", expr("list")).input("val", "v1").input("number", 2020)
			.add(logic("INSERT").inputs("list", expr("list"), "val", "v1").build())
			.add(logic("INSERT").inputs("list", expr("list"), "val", "v1")
					.retry(retry(max(5), seconds(3)).includeCodes(100,234).andMessages("error1","dfgdg").build())
					.delayed(5)
					.fork()
//					.fork("F1")
					.join(args)
					.timeout(5)
					.label("L1")
					.handle(error().withCodes(100,101).orMessages("error1", "error2").usingLogic(logic("ERROR").build()).build())
					.build())
			.add(exit().build())
			.add(fail().withCode(200).andMessage("failure").build())
			.add(branch(when("account == 222"), logic("INSERT").inputs("list", expr("list"), "val", "v1").build()).build())
			.add(group(
				branch(expr("a > 5"), 
						logic("INSERT")
						.input("list", expr("list")).input("val", "v1").input("number", 2020)
						.delayed(5)
						.handle(error().withCodes(100,101).orMessages("error1", "error2").usingLogic(logic("ERROR").build()).build())
						.fork("F1")
						.join("F2")
						.label("L1")
						.retry(retry(max(5), seconds(3)).includeCodes(100).andMessages("error1").build())
						.timeout(5)
						.build()).build(),
				exit().build(),
				fail().withCode(200).andMessage("failure").build(),
				jump("L1").build(),
				savepoint().build(),
				verify(expr("false")).elseFailWithCode(100).andMessage("failure").build()
//				process("SIMPLE_SCENARIO_1", 0).build()
				).build())
			.build();
	
		createProcess("SIMPLE_SCENARIO_2", 0)
	    .inputs("val", "v1", "number", 2020, "list", expr("list"))
	    .add(logic("INSERT").inputs("val", "v1", "number", 2020, "list", expr("list")).build())
	    .add(logic("INSERT").inputs("val", "v1", "number", 2020, "list", expr("list")).retry(retry(max(5), seconds(3)).includeCodes(234, 100).andMessages("dfgdg", "error1").build()).handle(error().withCodes(101, 100).orMessages("error2", "error1").usingLogic(logic("ERROR").inputs().build()).build()).delayed(seconds(5)).fork().join().label("L1").timeout(seconds(5)).build())
	    .add(exit().build())
	    .add(fail().withCode(200).andMessage("failure").build())
	    .add(branch(when("account == 222"), logic("INSERT").inputs("val", "v1", "list", expr("list")).build()).build())
	    .add(group(branch(when("a > 5"), logic("INSERT").inputs("val", "v1", "number", 2020, "list", expr("list")).retry(retry(max(5), seconds(3)).includeCodes(100).andMessages("error1").build()).handle(error().withCodes(101, 100).orMessages("error2", "error1").usingLogic(logic("ERROR").inputs().build()).build()).delayed(seconds(5)).fork("F1").join("F2").label("L1").timeout(seconds(5)).build()).build(), exit().build(), fail().withCode(200).andMessage("failure").build(), jump("L1").build(), savepoint().build(), verify(when("false")).elseFailWithCode(100).andMessage("failure").build()).build())
	.build();

		createProcess("SIMPLE_SCENARIO_2", 0)
	    .inputs()
	    .add(group(branch(when("a > 5"), logic("INSERT").inputs("val", "v1", "number", 2020, "list", expr("list")).retry(retry(max(5), seconds(3)).includeCodes(100).andMessages("error1").build()).handle(error().withCodes(101, 100).orMessages("error2", "error1").usingLogic(logic("ERROR").inputs().build()).build()).delayed(seconds(5)).fork("F1").join("F2").label("L1").timeout(seconds(5)).build()).build(), exit().build(), fail().withCode(200).andMessage("failure").build(), jump("L1").build(), savepoint().build(), verify(when("false")).elseFailWithCode(100).andMessage("failure").build(), group().build()).build())
	.build();

		
		System.out.println(new ProcessDefinitionWriter(scenario1()).write());
		
	}
	
	public static ProcessDefinition scenario2() {
		return createProcess("SIMPLE_SCENARIO_2", 0)
				.add(group(
				branch(expr("a > 5"), 
						logic("INSERT")
						.input("list", expr("list")).input("val", "v1").input("number", 2020)
						.delayed(5)
						.handle(error().withCodes(100,101).orMessages("error1", "error2").usingLogic(logic("ERROR").build()).build())
						.fork("F1")
						.join("F2")
						.label("L1")
						.retry(retry(5, 3).includeCodes(100).andMessages("error1").build())
						.timeout(5)
						.build()).build(),
					exit().build(),
					fail().withCode(200).andMessage("failure").build(),
					jump("L1").build(),
					savepoint().build(),
					verify(expr("false")).elseFailWithCode(100).andMessage("failure").build(),
					process("SIMPLE_SCENARIO_1", 0).build()
				).build()).build();

	}
	
	public static ProcessDefinition scenario1() {
		return createProcess("SIMPLE_SCENARIO_1", 0)
				.add(logic("INSERT").input("list", expr("list")).input("val", "v1").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v2").build())
				.add(logic("INSERT").input("list", expr("list")).input("val", "v3").build())
				.build();
	}
}
