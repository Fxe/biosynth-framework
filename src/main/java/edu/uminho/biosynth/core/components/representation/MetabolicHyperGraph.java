package edu.uminho.biosynth.core.components.representation;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;
//import edu.uminho.biosynth.core.components.representation.basic.hypergraph.DiHyperEdge;
import edu.uminho.biosynth.core.components.representation.basic.hypergraph.DiHyperGraph;
import edu.uminho.biosynth.core.components.representation.basic.hypergraph.ReactionEdge;

public class MetabolicHyperGraph extends DiHyperGraph<String, String> implements IMetabolicRepresentation {

	private final static Logger LOGGER = Logger.getLogger(MetabolicHyperGraph.class.getName());
	
	public static final String normTag = "";
	public static final String reveTag = "R";
	
	@Override
	public boolean addMetabolite(GenericMetabolite cpd) {
		return this.addVertice( cpd.getEntry());
	}
	
	public ReactionEdge createEdge(GenericReaction rxn, boolean leftToRight) {

		String edgeName = rxn.getEntry() + ( leftToRight ? normTag:reveTag);
		
		ReactionEdge edge = null;
		if ( leftToRight) {
			LOGGER.log(Level.INFO, "Creating EDGE " + edgeName + " => ");
//			edge = new ReactionEdge( rxn.getReactantsID(), rxn.getProductsID(), edgeName, rxn.getId());
		} else {
			LOGGER.log(Level.INFO, "Creating EDGE " + edgeName + " <= ");
//			edge = new ReactionEdge( rxn.getProductsID(), rxn.getReactantsID(), edgeName, rxn.getId());
		}
		return edge;
	}
	
	@Override
	public boolean addReaction(GenericReaction rxn) {
		return this.addReaction(rxn, false);
	}
	@Override
	public boolean addReaction(GenericReaction rxn, boolean duplicateForReverse) {
		LOGGER.log(Level.INFO, "Add Reaction " + rxn.getEntry() + (duplicateForReverse? " WITH reverse span":" NO reverse span"));
		
//		boolean origOrientation = rxn.getOrientation() >= 0; // 0, 1, 2, 3 etc ARE LEFT TO RIGHT
//		ReactionEdge edge = this.createEdge(rxn, origOrientation);
		
//		if ( duplicateForReverse && rxn.isReversible()) {
//			LOGGER.log(Level.INFO, "Add Reaction " + rxn.getId() + " is reversible");
//			// CREATE opposite Direction EDGE'
//			DiHyperEdge<String, String> edge_ = createEdge(rxn, !origOrientation);
//			if ( !this.addEdge(edge_)) {
//				LOGGER.log(Level.SEVERE, "Error Adding Reverse Edge: " + rxn.getId());
//			}
//		}
//		if ( !this.addEdge(edge)) {
//			LOGGER.log(Level.SEVERE, "Error Adding Normal Edge: " + rxn.getId());
//			return false;
//		}
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
