package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynthframework.GenericReaction;

public class IntegratedReactionEntity extends GenericReaction {
	
	private static final long serialVersionUID = 1L;
	
	public enum MappingType {
		MEMBER, CLUSTER
	}

	private Map<String, Map<Object, List<Long>>> properties = new HashMap<> ();
	
	public Map<String, Map<Object, List<Long>>> getProperties() { return properties;}
	public void setProperties(Map<String, Map<Object, List<Long>>> properties) {
		this.properties = properties;
	}

	private Map<Long, MappingType> metaboliteMappingType = new HashMap<> ();
	
	
	public Map<Long, MappingType> getMetaboliteMappingType() {
		return metaboliteMappingType;
	}
	public void setMetaboliteMappingType(
			Map<Long, MappingType> metaboliteMappingType) {
		this.metaboliteMappingType = metaboliteMappingType;
	}

	private Map<Long, Set<Long>> metaboliteMapping = new HashMap<> ();
	public Map<Long, Set<Long>> getMetaboliteMapping() {
		return metaboliteMapping;
	}
	public void setMetaboliteMapping(Map<Long, Set<Long>> metaboliteMapping) {
		this.metaboliteMapping = metaboliteMapping;
	}

	private Map<Long, Map<Long, Double>> leftUnifiedStoichiometry = new HashMap<> ();
	public Map<Long, Map<Long, Double>> getLeftUnifiedStoichiometry() {
		return leftUnifiedStoichiometry;
	}
	public void setLeftUnifiedStoichiometry(
			Map<Long, Map<Long, Double>> leftUnifiedStoichiometry) {
		this.leftUnifiedStoichiometry = leftUnifiedStoichiometry;
	}


	private Map<Long, Map<Long, Double>> rightUnifiedStoichiometry = new HashMap<> ();
	public Map<Long, Map<Long, Double>> getRightUnifiedStoichiometry() {
		return rightUnifiedStoichiometry;
	}
	public void setRightUnifiedStoichiometry(
			Map<Long, Map<Long, Double>> rightUnifiedStoichiometry) {
		this.rightUnifiedStoichiometry = rightUnifiedStoichiometry;
	}

	private Map<Long, CompositeProxyId> sourcesMap = new HashMap<> ();
	public Map<Long, CompositeProxyId> getSourcesMap() {
		return sourcesMap;
	}
	public void setSourcesMap(Map<Long, CompositeProxyId> sourcesMap) {
		this.sourcesMap = sourcesMap;
	}

	private Map<Long, String> equation = new HashMap<> ();
	
	private Map<Long, ?> left = new HashMap<> ();
	private Map<Long, ?> right = new HashMap<> ();
	
	
}
