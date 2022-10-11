package dev.linkedlogics.model.process;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Setter(value = AccessLevel.PACKAGE)
@Getter(value = AccessLevel.PUBLIC)
public abstract class BaseLogicDefinition implements Cloneable {
	protected String position;
	protected boolean isDisabled;
	protected Map<String, Object> inputMap = new HashMap<>();
	protected Map<String, Object> outputMap = new HashMap<>();
	protected ForkLogicDefinition fork;
	protected JoinLogicDefinition join;
	protected boolean forced;
	protected RetryLogicDefinition retry;
	protected DelayLogicDefinition delay;
	protected ErrorLogicDefinition error;
	
	protected BaseLogicDefinition parentLogic;
	protected BaseLogicDefinition adjacentLogic;
	
	public BaseLogicDefinition cloneLogic() {
		BaseLogicDefinition clone = clone();
		clone.setDisabled(isDisabled);
		return clone;
	}
	
	protected abstract BaseLogicDefinition clone() ;
	
	public Optional<BaseLogicDefinition> getParentLogic() {
		return Optional.ofNullable(parentLogic);
	}
	
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
			this.logic.getInputMap().put(key, value);
			return (T) this;
		}
		
		public T inputs(Map<String, Object> inputs) {
			this.logic.getInputMap().putAll(inputs);
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
		
		public T handle(ErrorLogicDefinition error) {
			this.logic.setError(error);
			return (T) this;
		}
		
		protected L getLogic() {
			return (L) this.logic;
		}
		
		public L build() {
			return (L) logic;
		}
	}
}
