package pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReactionEdge extends DiHyperEdge<String, String> {
	
	protected Map<String, Double> headStoichiometry = new HashMap<> ();
	protected Map<String, Double> tailStoichiometry = new HashMap<> ();
	
	private String reactionId;

	public ReactionEdge(Set<String> in, Set<String> out, String body, String rxnId) {
		super(in, out, body);
		this.reactionId = rxnId;
	}
	
	public ReactionEdge(
			String[] in, String[] out, 
			Double[] inStoich, Double[] outStoich, 
			String body, String rxnId) {
		
		super(in, out, body);
		for (int i = 0; i < in.length; i++) this.headStoichiometry.put(in[i], inStoich[i]);
		for (int i = 0; i < out.length; i++) this.tailStoichiometry.put(out[i], outStoich[i]);
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

	public Map<String, Double> getHeadStoichiometry() {
		return headStoichiometry;
	}

	public void setHeadStoichiometry(Map<String, Double> headStoichiometry) {
		this.headStoichiometry = headStoichiometry;
	}

	public Map<String, Double> getTailStoichiometry() {
		return tailStoichiometry;
	}

	public void setTailStoichiometry(Map<String, Double> tailStoichiometry) {
		this.tailStoichiometry = tailStoichiometry;
	}

	@Override
	public String toString() {
		String str = String.format("ReactionEdge[%s]<%s, %s>", 
				this.getBody(), this.getHeadStoichiometry(), this.getTailStoichiometry());
		return str;
	}
}
