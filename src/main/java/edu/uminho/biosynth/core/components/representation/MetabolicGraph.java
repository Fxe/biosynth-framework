package edu.uminho.biosynth.core.components.representation;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultBinaryEdge;
import edu.uminho.biosynth.core.components.representation.basic.graph.DefaultGraphImpl;

public class MetabolicGraph extends DefaultGraphImpl<String, String> implements IMetabolicRepresentation {

	public static String normTag = "";
	public static String reveTag = "R";
	
	public MetabolicGraph() {
		super();
	}
	
	public MetabolicGraph(MetabolicGraph graph) {
		super(graph);
	}
	
	@Override
	public boolean addReaction(GenericReaction rxn) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean addReaction(GenericReaction rxn, boolean duplicateForReverse) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean addReactionPair(GenericReactionPair rpr) {
		DefaultBinaryEdge<String, String> edge = 
				new DefaultBinaryEdge<String, String>( rpr.getEntry(), rpr.getEntry1().getEntry(), rpr.getEntry2().getEntry());
		return this.addEdge(edge);
	}
	
	@Override
	public boolean addReactionPair(GenericReactionPair rpr, boolean duplicateForReverse) {
		String id = rpr.getEntry();
		String normId = id + normTag;
		DefaultBinaryEdge<String, String> edge = 
				new DefaultBinaryEdge<String, String>( normId, rpr.getEntry1().getEntry(), rpr.getEntry2().getEntry());
		
		if (duplicateForReverse) {
			String reveId = id + reveTag;
			String left = edge.getRight();
			String right = edge.getLeft();
			double w = edge.getWeight();
			DefaultBinaryEdge<String, String> revEdge = new DefaultBinaryEdge<String, String>(reveId, left, right, w);
			if ( !this.addEdge(revEdge)) {
				System.err.println("ERROR ADD REVERSE:" + rpr);
			}
		}
		
		return this.addEdge(edge);
	}

	@Override
	public boolean addMetabolite(GenericMetabolite cpd) {
		return this.addVertex(cpd.getEntry());
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
		return true;
	}
}
