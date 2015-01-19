package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.GenericReaction;

public class IntegratedReactionEntity extends GenericReaction {
	
	private static final long serialVersionUID = 1L;

	private Map<String, Map<Object, List<Long>>> properties = new HashMap<> ();
	
	public Map<String, Map<Object, List<Long>>> getProperties() { return properties;}
	public void setProperties(Map<String, Map<Object, List<Long>>> properties) {
		this.properties = properties;
	}

	private Map<Long, Set<Long>> metaboliteMapping = new HashMap<> ();
	private Map<String, Map<String, Double>> leftUnifiedStoichiometry = new HashMap<> ();
	public Map<String, Map<String, Double>> getLeftUnifiedStoichiometry() {
		return leftUnifiedStoichiometry;
	}
	public void setLeftUnifiedStoichiometry(
			Map<String, Map<String, Double>> leftUnifiedStoichiometry) {
		this.leftUnifiedStoichiometry = leftUnifiedStoichiometry;
	}
	
	private Map<String, Map<String, Double>> rightUnifiedStoichiometry = new HashMap<> ();
	public Map<String, Map<String, Double>> getRightUnifiedStoichiometry() {
		return rightUnifiedStoichiometry;
	}
	public void setRightUnifiedStoichiometry(
			Map<String, Map<String, Double>> rightUnifiedStoichiometry) {
		this.rightUnifiedStoichiometry = rightUnifiedStoichiometry;
	}

	private Map<Long, CompositeProxyId> sourcesMap = new HashMap<> ();
	
	private Map<Long, String> equation = new HashMap<> ();
	
	private Map<Long, ?> left = new HashMap<> ();
	private Map<Long, ?> right = new HashMap<> ();
	
	
}
