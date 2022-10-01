package dev.linkedlogics.service.process.handler;

import java.util.Optional;

import dev.linkedlogics.model.process.BaseLogicDefinition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class HandlerResult {
	private Optional<String> nextCandidatePosition;
	private Optional<BaseLogicDefinition> selectedLogic;	
	private boolean endOfCandidates;
	
	public static HandlerResult nextCandidate(String candidate) {
		return new HandlerResult(Optional.ofNullable(candidate), Optional.empty(), false);
	}
	
	public static HandlerResult selectCandidate(Optional<BaseLogicDefinition> selected) {
		return new HandlerResult(Optional.empty(), selected, false);
	}
	
	public static HandlerResult noCandidate() {
		return new HandlerResult(Optional.empty(), Optional.empty(), false);
	}
	
	public static HandlerResult endOfCandidates() {
		return new HandlerResult(Optional.empty(), Optional.empty(), true);
	}
}
