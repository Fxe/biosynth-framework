package edu.uminho.biosynth.core.components;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class GenericReaction extends AbstractGenericEntity implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	public static boolean ORIENTATION_NORMAL  = true;
	public static boolean ORIENTATION_REVERSE = false;
	public static boolean ORIENTATION_LEFT_TO_RIGHT  = true;
	public static boolean ORIENTATION_RIGHT_TO_LEFT = false;
	
	public static String LEFT_TO_RIGHT = "LR";
	public static String RIGHT_TO_LEFT = "RL";

	@Column(name="REV") protected int orientation;

	@ElementCollection
	@CollectionTable(name="MNX_REACTION_ENZYME", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="ENZYME")
	protected List<String> enzymes = new ArrayList<> ();
	
	@Transient private Set<String> similarRxn;
	
//	private Map<String, GenericMetabolite>		cpdMap;
//	private Map<String, GenericEnzyme>			ecnMap;
//	private Map<String, GenericReactionPair>	rprMap;
	
	public int getOrientation() {
		return this.orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	
	public List<String> getEnzymes() {
		return enzymes;
	}
	public void addEnzyme(String enzyme) {
		this.enzymes.add(enzyme);
	}
	public void setEnzymes(List<String> enzymes) {
		this.enzymes = enzymes;
	}
	
	public String getFullDetails() {
		StringBuilder ret = new StringBuilder();
		ret.append("Key: ").append( this.id).append('\n');
		ret.append("ID: ").append(this.getEntry()).append('\n');
		ret.append("Name: ").append(this.name).append('\n');
//		ret.append("Equation: ").append(this.equation).append('\n');
//		ret.append("Left: ").append(this.left).append('\n');
//		ret.append("Right: ").append(this.right).append('\n');
//		ret.append("Similar: ").append(this.similarRxn).append('\n');
//		ret.append("Enzyme: ").append(this.ecnMap.keySet()).append('\n');
//		ret.append("RPairs: ").append(this.rprMap.keySet()).append('\n');
		return ret.toString();
	}
	
	

}
