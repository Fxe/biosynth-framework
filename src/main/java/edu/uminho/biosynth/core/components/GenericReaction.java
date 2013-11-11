package edu.uminho.biosynth.core.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

@MappedSuperclass
public class GenericReaction<M extends GenericMetabolite> extends AbstractGenericEntity implements Serializable{
	
	private static final long serialVersionUID = 114867769L;
	
	public static boolean ORIENTATION_NORMAL  = true;
	public static boolean ORIENTATION_REVERSE = false;
	public static boolean ORIENTATION_LEFT_TO_RIGHT  = true;
	public static boolean ORIENTATION_RIGHT_TO_LEFT = false;
	
	public static String LEFT_TO_RIGHT = "LR";
	public static String RIGHT_TO_LEFT = "RL";

	@Column(name="REV") protected int orientation;
	
	private String equation;

	private List<StoichiometryPair<M>> left;
	private List<StoichiometryPair<M>> right;
	
	private Set<String> similarRxn;
	
	private Map<String, GenericMetabolite>		cpdMap;
	private Map<String, GenericEnzyme>			ecnMap;
	private Map<String, GenericReactionPair>	rprMap;
	
	public GenericReaction() {
	
	}
	
	public GenericReaction(String id) {
		super(id);
		this.id = 0;
		this.orientation = 0;
		this.cpdMap = new HashMap<>();
		this.rprMap = new HashMap<>();
		this.ecnMap = new HashMap<>();
		this.name = "unnamed";
		this.similarRxn = new HashSet<String> ();
	}
	
	public GenericReaction(String id, int key) {
		super(id);
		this.id = key;
		this.orientation = 0;
		this.cpdMap = new HashMap<>();
		this.rprMap = new HashMap<>();
		this.ecnMap = new HashMap<>();
		this.rprMap = new HashMap<String, GenericReactionPair>();
		this.ecnMap = new HashMap<String, GenericEnzyme>();
		this.name = "unnamed";
		this.similarRxn = new HashSet<String> ();
	}

	
	public int getOrientation() {
		return this.orientation;
	}
	public void setOrientation(int orientation) {
		this.orientation = orientation;
	}
	
	public String getFullDetails() {
		StringBuilder ret = new StringBuilder();
		ret.append("Key: ").append( this.id).append('\n');
		ret.append("ID: ").append(this.getEntry()).append('\n');
		ret.append("Name: ").append(this.name).append('\n');
		ret.append("Equation: ").append(this.equation).append('\n');
		ret.append("Left: ").append(this.left).append('\n');
		ret.append("Right: ").append(this.right).append('\n');
		ret.append("Similar: ").append(this.similarRxn).append('\n');
		ret.append("Enzyme: ").append(this.ecnMap.keySet()).append('\n');
		ret.append("RPairs: ").append(this.rprMap.keySet()).append('\n');
		return ret.toString();
	}
	
	

}
