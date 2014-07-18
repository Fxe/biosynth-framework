package edu.uminho.biosynth.core.components.representation.basic.hypergraph;

import java.util.HashMap;
import java.util.Map;

/**
 * Metabolic Directed HyperEdge. HyperEdge representation for
 * metabolic reaction. Contains access to the reverse edge 
 * to map reversible reactions by duplication. Additionally
 * contains information of the stoichiometry of the connected
 * metabolites.
 * 
 * @author Filipe Liu
 *
 * @param <V>
 * @param <E>
 */
public class MetabolicHyperEdge<V, E> extends DiHyperEdge <V, E> {

	protected Map<V, Double> headStoichiometry = new HashMap<> ();
	protected Map<V, Double> tailStoichiometry = new HashMap<> ();
	
	public MetabolicHyperEdge(V[] in, V[] out, E body) {
		super(in, out, body);
		
		for (V h : this.head_) this.headStoichiometry.put(h, 1.0d);
		for (V h : this.tail_) this.tailStoichiometry.put(h, 1.0d);
	}
}
