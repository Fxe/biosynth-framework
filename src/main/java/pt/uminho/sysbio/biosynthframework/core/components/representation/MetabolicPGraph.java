package pt.uminho.sysbio.biosynthframework.core.components.representation;

//import java.util.Set;

import java.util.HashSet;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;
import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.GenericReactionPair;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.OperatingUnit;
import pt.uminho.sysbio.biosynthframework.core.components.representation.basic.pgraph.ProcessGraph;
//import edu.uminho.biosynth.core.components.representation.basic.pgraph.OperatingUnit;

public class MetabolicPGraph extends ProcessGraph<String> implements IMetabolicRepresentation {

	public static final String normTag = "";
	public static final String reveTag = "_R";
	
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
		Set<String> alpha = new HashSet<> (rxn.getReactantStoichiometry().keySet());
		Set<String> beta = new HashSet<> (rxn.getProductStoichiometry().keySet());
		OperatingUnit<String> op = new OperatingUnit<String>(alpha, beta);
		
//		String edgeName = rxn.getEntry() + ( leftToRight ? normTag:reveTag);
		
		op.setID(rxn.getEntry() + normTag);
		System.out.println(op);
		if ( duplicateForReverse ) {
			Set<String> alpha_ = new HashSet<> (rxn.getProductStoichiometry().keySet());
			Set<String> beta_ = new HashSet<> (rxn.getReactantStoichiometry().keySet());
			OperatingUnit<String> op_ = new OperatingUnit<String>(alpha_, beta_);
			op_.setID(rxn.getEntry() + reveTag);
			op.setOpposite(op_);
			op_.setOpposite(op);
			System.out.println(op_);
			if ( !this.addOperatingUnit(op_)) {
				System.err.println("ERROR ADD REVERSE: " + rxn);
			}
		}
		return this.addOperatingUnit(op);
		
//		return false;
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
