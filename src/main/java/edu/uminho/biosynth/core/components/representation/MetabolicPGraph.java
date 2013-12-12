package edu.uminho.biosynth.core.components.representation;

//import java.util.Set;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;
//import edu.uminho.biosynth.core.components.representation.basic.pgraph.OperatingUnit;
import edu.uminho.biosynth.core.components.representation.basic.pgraph.ProcessGraph;

public class MetabolicPGraph extends ProcessGraph<String> implements IMetabolicRepresentation {

	@Override
	public boolean isReactionPair() {
		// TODO Auto-generated method stub
		return false;
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
	public boolean addReaction(GenericReaction rxn) {
		return addReaction(rxn, false);
	}
	
	@Override
	public boolean addReaction(GenericReaction rxn, boolean duplicateForReverse) {
//		Set<String> alpha = rxn.getReactantsID();
//		Set<String> beta = rxn.getProductsID();
//		OperatingUnit<String> op = new OperatingUnit<String>(alpha, beta);
//		op.setID(rxn.getId());
//		if ( duplicateForReverse ) {
//			Set<String> alpha_ = rxn.getProductsID();
//			Set<String> beta_ = rxn.getReactantsID();
//			OperatingUnit<String> op_ = new OperatingUnit<String>(alpha_, beta_);
//			op_.setID(rxn.getId() + "R");
//			op.setOpposite(op_);
//			op_.setOpposite(op);
//			if ( !this.addOperatingUnit(op_)) {
//				System.err.println("ERROR ADD REVERSE: " + rxn);
//			}
//		}
//		return this.addOperatingUnit(op);
		
		return false;
	}

	@Override
	public boolean addMetabolite(GenericMetabolite cpd) {
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
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("SIZE: ").append( this.size()).append(" ORDER: ").append( this.order());
		return sb.toString();
	}

}
