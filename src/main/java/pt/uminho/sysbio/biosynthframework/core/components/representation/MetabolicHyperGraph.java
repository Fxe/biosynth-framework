package pt.uminho.sysbio.biosynthframework.core.components.representation;

import org.apache.log4j.Logger;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.DiHyperGraph;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.hypergraph.ReactionEdge;
//import edu.uminho.biosynth.core.components.representation.basic.hypergraph.DiHyperEdge;

public class MetabolicHyperGraph extends DiHyperGraph<String, String> implements IMetabolicRepresentation {

	private static Logger LOGGER = Logger.getLogger(MetabolicHyperGraph.class);
	
	public static final String normTag = "";
	public static final String reveTag = "R";
	
	public MetabolicHyperGraph() {}
	
	public MetabolicHyperGraph(MetabolicHyperGraph metabolicHyperGraph) {
		super(metabolicHyperGraph);
	}
	
	@Override
	public boolean addMetabolite(GenericMetabolite cpd) {
		return this.addVertice( cpd.getEntry());
	}
	
	public ReactionEdge createEdge(GenericReaction rxn, boolean leftToRight) {

		String edgeName = rxn.getEntry() + ( leftToRight ? normTag:reveTag);
		
		ReactionEdge edge = null;
		String[] in = new String[rxn.getReactantStoichiometry().size()];
		String[] out = new String[rxn.getProductStoichiometry().size()];
		Double[] inStoich  = new Double[in.length];
		Double[] outStoich = new Double[out.length];
		int ptr;
		
		ptr = 0;
		for (String id : rxn.getReactantStoichiometry().keySet()) {
			in[ptr] = id;
			inStoich[ptr] = rxn.getReactantStoichiometry().get(id);
			ptr++;
		}
		
		ptr = 0;
		for (String id : rxn.getProductStoichiometry().keySet()) {
			out[ptr] = id;
			outStoich[ptr] = rxn.getProductStoichiometry().get(id);
			ptr++;
		}
		
		if ( leftToRight) {
			LOGGER.trace("Creating EDGE " + edgeName + " => ");
			edge = new ReactionEdge( in, out, inStoich, outStoich, edgeName, rxn.getEntry());
		} else {
			LOGGER.trace("Creating EDGE " + edgeName + " <= ");
			edge = new ReactionEdge( out, in, outStoich, inStoich, edgeName, rxn.getEntry());
		}
		return edge;
	}
	
	@Override
	public boolean addReaction(GenericReaction rxn) {
		return this.addReaction(rxn, false);
	}
	
	@Override
	public boolean addReaction(GenericReaction rxn, boolean duplicateForReverse) {
		LOGGER.trace("Add Reaction " + rxn.getEntry() + (duplicateForReverse? " WITH reverse span":" NO reverse span"));
		
//		boolean origOrientation = rxn.getOrientation() >= 0; // 0, 1, 2, 3 etc ARE LEFT TO RIGHT
		ReactionEdge edge = this.createEdge(rxn, true);
		
		if ( duplicateForReverse) {
			LOGGER.trace("Add Reaction " + rxn.getEntry() + " is reversible");
			// CREATE opposite Direction EDGE'
			ReactionEdge edge_ = createEdge(rxn, false);
			if ( !this.addEdge(edge_)) {
				LOGGER.warn("Error Adding Reverse Edge: " + rxn.getId());
			}
		}
		if ( !this.addEdge(edge)) {
			LOGGER.trace("Error Adding Normal Edge: " + rxn.getId());
			return false;
		}
		return true;
	}
	
	@Override
	public boolean addReactionPair(GenericReactionPair rpr) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean addReactionPair(GenericReactionPair rpr,
			boolean duplicateForReverse) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeReaction() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeMetabolite() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public final boolean isReactionPair() {
		return false;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SIZE: ").append( this.size()).append(" ORDER: ").append( this.order());
		return sb.toString();
	}
}
