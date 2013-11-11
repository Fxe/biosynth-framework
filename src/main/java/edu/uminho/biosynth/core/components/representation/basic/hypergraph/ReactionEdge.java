package edu.uminho.biosynth.core.components.representation.basic.hypergraph;

import java.util.Set;

public class ReactionEdge extends DiHyperEdge<String, String> {
	
	private String reactionId;

	public ReactionEdge(Set<String> in, Set<String> out, String body, String rxnId) {
		super(in, out, body);
		this.reactionId = rxnId;
	}
	
	public ReactionEdge(String[] in, String[] out, String body, String rxnId) {
		super(in, out, body);
		this.reactionId = rxnId;
	}
	
	public ReactionEdge( ReactionEdge arc) {
		super(arc);
		this.reactionId = arc.getReactionId();
	}

	public String getReactionId() {
		return reactionId;
	}
	public void setReactionId(String reactionId) {
		this.reactionId = reactionId;
	}

}
