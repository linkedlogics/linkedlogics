package io.linkedlogics.model.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class BaseLogicDefinition extends TypedLogicDefinition implements Cloneable {
	@JsonIgnore
	protected String position;
	protected Boolean disabled = Boolean.FALSE;
	protected Map<String, Object> inputs = new HashMap<>();
	protected Map<String, Object> outputs = new HashMap<>();
	protected ForkLogicDefinition fork;
	protected JoinLogicDefinition join;
	protected Boolean forced = Boolean.FALSE;
	protected RetryLogicDefinition retry;
	protected DelayLogicDefinition delay;
	protected ErrorLogicDefinition error;
	protected TimeoutLogicDefinition timeout;
	protected LabelLogicDefinition label;
	protected String id;
	protected String desc;
	
	public BaseLogicDefinition(String type) {
		super(type);
	}
	
	@JsonIgnore
	protected BaseLogicDefinition parentLogic;
	@JsonIgnore
	protected BaseLogicDefinition adjacentLogic;
	
	public final BaseLogicDefinition cloneLogic() {
		BaseLogicDefinition clone = clone();
		clone.setDisabled(getDisabled());
		clone.setFork(fork != null ? fork.cloneLogic() : null);
		clone.setJoin(join != null ? join.cloneLogic() : null);
		clone.setForced(getForced());
		clone.setRetry(retry != null ? retry.cloneLogic() : null);
		clone.setDelay(delay != null ? delay.cloneLogic() : null);
		clone.setError(error != null ? error.cloneLogic() : null);
		clone.getInputs().putAll(getInputs());
		clone.getOutputs().putAll(getOutputs());
		return clone;
	}
	
	protected abstract BaseLogicDefinition clone() ;
	
	public Optional<BaseLogicDefinition> getParentLogic() {
		return Optional.ofNullable(parentLogic);
	}
	
	@Getter
	public static class LogicBuilder<T, L extends BaseLogicDefinition> {
		protected L logic;
		
		public LogicBuilder(L logic) {
			this.logic = logic;
		}
		
		public T disabled() {
			this.logic.setDisabled(true);
			return (T) this;
		}
		
		public T input(String key, Object value) {
			this.logic.getInputs().put(key, value);
			return (T) this;
		}
		
		public T inputs(Object... inputs) {
			if (inputs.length % 2 == 0) {
				IntStream.range(0, inputs.length / 2).forEach(i -> {
					this.logic.getInputs().put((String) inputs[i * 2], inputs[i * 2 + 1]);
				});
				return (T) this;
			} else {
				throw new IllegalArgumentException("parameters size must be even");
			}
		}
		
		public T inputs(Map<String, Object> inputs) {
			this.logic.getInputs().putAll(inputs);
			return (T) this;
		}
		
		public T join(String... join) {
			this.logic.setJoin(new JoinLogicDefinition(join));
			return (T) this;
		}
		
		public T forced() {
			this.logic.setForced(true);
			return (T) this;
		}
		
		public T delayed(int delay) {
			this.logic.setDelay(new DelayLogicDefinition(delay));
			return (T) this;
		}
		
		public T handle(ErrorLogicDefinition error) {
			this.logic.setError(error);
			return (T) this;
		}
		
		public T timeout(int timeout) {
			this.logic.setTimeout(new TimeoutLogicDefinition(timeout));
			return (T) this;
		}
		
		public T label(String label) {
			this.logic.setLabel(new LabelLogicDefinition(label));
			return (T) this;
		}
		
		public T id(String id) {
			this.logic.setId(id);
			return (T) this;
		}
		
		public T desc(String desc) {
			this.logic.setDesc(desc);
			return (T) this;
		}
		
		public L build() {
			return (L) logic;
		}
	}
}