package dev.linkedlogics.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

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
import dev.linkedlogics.model.process.ProcessLogicDefinition;
import dev.linkedlogics.model.process.RetryLogicDefinition;
import dev.linkedlogics.model.process.SavepointLogicDefinition;
import dev.linkedlogics.model.process.SingleLogicDefinition;
import dev.linkedlogics.model.process.TimeoutLogicDefinition;
import dev.linkedlogics.model.process.VerifyLogicDefinition;
import dev.linkedlogics.service.LogicService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ProcessDefinitionWriter {
	private static String TAB = "    ";
	private static String LINE = "\n";
	private static String BUILD = ".build()";
	private ProcessDefinition process;

	public String write() {
		StringBuilder builder = new StringBuilder("import static dev.linkedlogics.LinkedLogicsBuilder.*;\n\n");
		write(builder, process);
		return builder.toString();
	}

	private void write(StringBuilder builder, ProcessDefinition process) {
		builder.append("return createProcess(\"").append(process.getId()).append("\", ").append(process.getVersion()).append(")\n");
		builder.append(TAB);
		write(builder, process.getInputs(), true);

		process.getLogics().forEach(l -> {
			builder.append(LINE).append(TAB).append(".add(");
			write(builder, l);
			builder.append(")");
		});

		builder.append(LINE).append(BUILD).append(";");
	}

	private void write(StringBuilder builder, BaseLogicDefinition logic) {
		if (logic instanceof SingleLogicDefinition) {
			SingleLogicDefinition single = (SingleLogicDefinition) logic;
			write(builder, single);

			if (single.getApplication() != null) {
				builder.append(".application(\"").append(single.getApplication()).append("\")");
			}

			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}

			if (single.getReturnAs() != null) {
				builder.append(".returnAs(\"").append(single.getReturnAs()).append("\")");
			}

			if (single.getCompensationLogic() != null) {
				builder.append(".compensate(");
				write(builder, single.getCompensationLogic());
				builder.append(")");
			}
		} else if (logic instanceof ProcessLogicDefinition) {
			write(builder, (ProcessLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else if (logic instanceof BranchLogicDefinition) {
			write(builder, (BranchLogicDefinition) logic);
		} else if (logic instanceof ExitLogicDefinition) {
			write(builder, (ExitLogicDefinition) logic);
		} else if (logic instanceof FailLogicDefinition) {
			write(builder, (FailLogicDefinition) logic);
		} else if (logic instanceof GroupLogicDefinition) {
			write(builder, (GroupLogicDefinition) logic);
			if (logic.getInputs() != null && !logic.getInputs().isEmpty()) {
				write(builder, logic.getInputs(), true);
			}
		} else if (logic instanceof JumpLogicDefinition) {
			write(builder, (JumpLogicDefinition) logic);
		} else if (logic instanceof SavepointLogicDefinition) {
			write(builder, (SavepointLogicDefinition) logic);
		} else if (logic instanceof VerifyLogicDefinition) {
			write(builder, (VerifyLogicDefinition) logic);
		} else {
			builder.append("MISSING TYPE ").append(logic.getType());
		}

		if (logic.getOutputs() != null && !logic.getOutputs().isEmpty()) {
			write(builder, logic.getOutputs(), false);
		}
		
		if (logic.getForced()) {
			builder.append(".forced()");
		}

		if (logic.getDelay() != null) {
			write(builder, logic.getDelay());
		}

		if (logic.getFork() != null) {
			write(builder, logic.getFork());
		}

		if (logic.getJoin() != null) {
			write(builder, logic.getJoin());
		}

		if (logic.getDisabled()) {
			builder.append(".disabled()");
		}

		if (logic.getLabel() != null) {
			write(builder, logic.getLabel());
		}

		if (logic.getTimeout() != null) {
			write(builder, logic.getTimeout());
		}

		if (logic.getRetry() != null) {
			write(builder, logic.getRetry());
		}

		if (logic.getError() != null) {
			write(builder, logic.getError());
		}

		builder.append(BUILD);
	}

	private void write(StringBuilder builder, RetryLogicDefinition retry) {
		builder.append(".retry(retry(max(").append(retry.getMaxRetries()).append("), seconds(").append(retry.getSeconds()).append("))");

		if (retry.getErrorCodeSet().size() > 0) {
			if (retry.isExclude()) {
				builder.append(".excludeCodes(").append(retry.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", ")));
			} else {
				builder.append(".includeCodes(").append(retry.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", ")));
			}
			builder.append(")");
		}

		if (retry.getErrorMessageSet().size() > 0) {
			builder.append(".andMessages(");
			builder.append(retry.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
			builder.append(")");
		}

		builder.append(BUILD).append(")");
	}

	private void write(StringBuilder builder, DelayLogicDefinition delay) {
		builder.append(".delayed(seconds(").append(delay.getSeconds()).append("))");
	}

	private void write(StringBuilder builder, ExitLogicDefinition exit) {
		builder.append("exit()");
	}

	private void write(StringBuilder builder, JumpLogicDefinition jump) {
		builder.append("jump(\"").append(jump.getTargetLabel()).append("\")");
	}

	private void write(StringBuilder builder, SavepointLogicDefinition jump) {
		builder.append("savepoint()");
	}

	private void write(StringBuilder builder, VerifyLogicDefinition verify) {
		builder.append("verify(when(\"").append(verify.getExpression().getExpression()).append("\"))");
		if (verify.getErrorCode() != null) {
			builder.append(".elseFailWithCode(").append(verify.getErrorCode()).append(")");

			if (verify.getErrorMessage() != null) {
				builder.append(".andMessage(\"").append(verify.getErrorMessage()).append("\")");
			}
		} else if (verify.getErrorMessage() != null) {
			builder.append(".elseFailWithMessage(\"").append(verify.getErrorMessage()).append("\")");
		}
	}

	private void write(StringBuilder builder, FailLogicDefinition fail) {
		builder.append("fail()");
		if (fail.getErrorCode() != null) {
			builder.append(".withCode(").append(fail.getErrorCode()).append(")");

			if (fail.getErrorMessage() != null) {
				builder.append(".andMessage(\"").append(fail.getErrorMessage()).append("\")");
			}
		} else if (fail.getErrorMessage() != null) {
			builder.append(".withMessage(\"").append(fail.getErrorMessage()).append("\")");
		}
	}

	private void write(StringBuilder builder, ForkLogicDefinition fork) {
		builder.append(".fork(").append(fork.getKey().map(k -> "\"" + k + "\"").orElse("")).append(")");
	}

	private void write(StringBuilder builder, JoinLogicDefinition join) {
		builder.append(".join(").append(Arrays.stream(join.getKey()).map(m -> "\"" + m + "\"").collect(Collectors.joining(", "))).append(")");
	}

	private void write(StringBuilder builder, TimeoutLogicDefinition timeout) {
		builder.append(".timeout(seconds(").append(timeout.getSeconds()).append("))");
	}

	private void write(StringBuilder builder, LabelLogicDefinition label) {
		builder.append(".label(\"").append(label.getLabel()).append("\")");
	}

	private void write(StringBuilder builder, ErrorLogicDefinition error) {
		builder.append(".handle(error()");

		if (!error.getErrorCodeSet().isEmpty()) {
			builder.append(".withCodes(").append(error.getErrorCodeSet().stream().map(i -> String.valueOf(i)).collect(Collectors.joining(", "))).append(")");

			if (!error.getErrorMessageSet().isEmpty()) {
				builder.append(".orMessages(");
				builder.append(error.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
				builder.append(")");
			}
		} else if (!error.getErrorMessageSet().isEmpty()) {
			builder.append(".withMessages(");
			builder.append(error.getErrorMessageSet().stream().map(m -> "\"" + m + "\"").collect(Collectors.joining(", ")));
			builder.append(")");
		}

		if (error.isThrowAgain()) {
			builder.append(".throwAgain(");
			if (error.getThrowErrorCode() != null) {
				builder.append(error.getThrowErrorCode()).append(", \"").append(error.getThrowErrorMessage()).append("\"");
			}
			builder.append(")");
		}

		if (error.getErrorLogic() != null) {
			builder.append(".usingLogic(");
			write(builder, error.getErrorLogic());
			builder.append(")");
		}

		builder.append(BUILD).append(")");
	}

	private void write(StringBuilder builder, BranchLogicDefinition branch) {
		builder.append("branch(when(\"").append(branch.getExpression().getExpression()).append("\"), ");
		write(builder, branch.getLeftLogic());
		if (branch.getRightLogic() != null) {
			builder.append(", ");
			write(builder, branch.getRightLogic());
		}
		builder.append(")");
	}

	private void write(StringBuilder builder, ProcessLogicDefinition process) {
		builder.append("process(\"").append(process.getProcessId()).append("\", ").append(process.getVersion()).append(")");
	}

	private void write(StringBuilder builder, GroupLogicDefinition group) {
		builder.append("group(");

		for (int i = 0; i < group.getLogics().size(); i++) {
			if (i > 0) {
				builder.append(", ");
			}
			write(builder, group.getLogics().get(i));
		}

		builder.append(")");
	}

	private void write(StringBuilder builder, SingleLogicDefinition logic) {
		builder.append("logic(\"").append(logic.getLogicId()).append("\"");
		if (logic.getLogicVersion() != LogicService.LATEST_VERSION) {
			builder.append(", ").append(logic.getLogicVersion());
		}
		builder.append(")");
	}

	private void write(StringBuilder builder, Map<String, Object> params, boolean isInput) {
		if (params != null && !params.isEmpty()) {
			if (isInput) {
				builder.append(".inputs(");
			} else {
				builder.append(".outputs(");
			}
			builder.append(params.entrySet().stream().map(e -> {
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
			builder.append(")");
		}
	}
}
