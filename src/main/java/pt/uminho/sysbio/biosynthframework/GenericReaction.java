package pt.uminho.sysbio.biosynthframework;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class GenericReaction extends AbstractBiosynthEntity 
implements Reaction, Serializable, Cloneable {
	
	private static final long serialVersionUID = 1L;
	
	@Enumerated(EnumType.STRING)
	@Column(name="orientation")
	protected Orientation orientation = Orientation.LeftToRight;
//	protected List<StoichiometryPair> left = new ArrayList<> ();
//	protected Map<M, Double> right = new HashMap<> ();
	
//	public static boolean ORIENTATION_NORMAL  = true;
//	public static boolean ORIENTATION_REVERSE = false;
//	public static boolean ORIENTATION_LEFT_TO_RIGHT  = true;
//	public static boolean ORIENTATION_RIGHT_TO_LEFT = false;
	
	public static String LEFT_TO_RIGHT = "LR";
	public static String RIGHT_TO_LEFT = "RL";

//	@Column(name="REV") protected int orientation;
	
	@Transient private Set<String> similarRxn;
	
	@Transient
	protected Map<String, Double> reactantStoichiometry = new HashMap<> ();
	
	@Transient
	protected Map<String, Double> productStoichiometry = new HashMap<> ();
	

//	@Override
//	public List<StoichiometryPair> getLeft() { return this.left;}
//	@Override
//	public Map<M, Double> getRight() { return this.right;}
	
//	private Map<String, GenericMetabolite>		cpdMap;
//	private Map<String, GenericEnzyme>			ecnMap;
//	private Map<String, GenericReactionPair>	rprMap;
	
	public Map<String, Double> getReactantStoichiometry() {
		return reactantStoichiometry;
	}
	public void setReactantStoichiometry(Map<String, Double> reactantStoichiometry) {
		this.reactantStoichiometry = reactantStoichiometry;
	}
	public Map<String, Double> getProductStoichiometry() {
		return productStoichiometry;
	}
	public void setProductStoichiometry(Map<String, Double> productStoichiometry) {
		this.productStoichiometry = productStoichiometry;
	}
	
	@Override
	public Orientation getOrientation() { return this.orientation; }
	public void setOrientation(Orientation orientation) { this.orientation = orientation;}
	
	public GenericReaction() { }
	
	public GenericReaction(GenericReaction rxn) {
		this.id = rxn.id;
		this.entry = rxn.entry;
		this.name = rxn.name;
		this.description = rxn.description;
		this.source = rxn.source;
		
		this.orientation = rxn.orientation;
		this.similarRxn = rxn.similarRxn == null ? null : new HashSet<> (rxn.similarRxn);
		this.productStoichiometry = rxn.productStoichiometry == null ? 
				null : new HashMap<> (rxn.productStoichiometry);
		this.reactantStoichiometry = rxn.reactantStoichiometry == null ? 
				null : new HashMap<> (rxn.reactantStoichiometry);
	}
	
	public String getFullDetails() {
		StringBuilder ret = new StringBuilder();
		ret.append("id: ").append( this.id).append('\n');
		ret.append("entry: ").append(this.getEntry()).append('\n');
		ret.append("name: ").append(this.name).append('\n');
//		ret.append("Equation: ").append(this.equation).append('\n');
//		ret.append("Left: ").append(this.left).append('\n');
//		ret.append("Right: ").append(this.right).append('\n');
//		ret.append("Similar: ").append(this.similarRxn).append('\n');
//		ret.append("Enzyme: ").append(this.ecnMap.keySet()).append('\n');
//		ret.append("RPairs: ").append(this.rprMap.keySet()).append('\n');
		return ret.toString();
	}
	
	@Override
	public Map<String, Double> getLeftStoichiometry() {
		return this.getReactantStoichiometry();
	}
	@Override
	public void setLeftStoichiometry(Map<String, Double> left) {
		this.setReactantStoichiometry(left);
	}
	@Override
	public Map<String, Double> getRightStoichiometry() {
		return this.getProductStoichiometry();
	}
	@Override
	public void setRightStoichiometry(Map<String, Double> right) {
		this.setProductStoichiometry(right);
	}	


//	@Override
//	public List<GenericMetabolite> getSubstrates() {
//		// TODO Auto-generated method stub
//		return null;
//	}
//	@Override
//	public List<GenericMetabolite> getReactants() {
//		// TODO Auto-generated method stub
//		return null;
//	}
	
//	@Override
//	public String toString() {
//		final char sep = '\n';
//		StringBuilder sb = new StringBuilder();
//		sb.append(super.toString()).append(sep);
//		sb.append("Orientation:")
//			.append('(')
//			.append(this.orientation)
//			.append(')')
//			.append(' ')
//			.append(this.orientation == 0 ? "L <-> R" : (this.orientation < 0 ? "L <-- R" : "L --> R" ));
//		return sb.toString();
//	}
	
	@Override
	public GenericReaction clone() {
		return new GenericReaction(this);
	}

}
