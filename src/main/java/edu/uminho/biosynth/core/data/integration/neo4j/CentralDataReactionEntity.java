package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.components.GenericReaction;

public class CentralDataReactionEntity extends GenericReaction {
	
	private static final long serialVersionUID = -1256662136005913037L;

	private String majorLabel;
	
	private Set<String> labels = new HashSet<> ();
	
	private Map<String, Object> properties = new HashMap<> ();

	private List<CentralDataReactionProperty> reactionProperties = new ArrayList<> ();
	private List<CentralDataReactionProperty> reactionStoichiometryProperties = new ArrayList<> ();
	
	public void addLabel(String label) {
		this.labels.add(label);
	}
	
	public String getMajorLabel() {
		return majorLabel;
	}

	public void setMajorLabel(String majorLabel) {
		this.majorLabel = majorLabel;
	}

	public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	
	
	public List<CentralDataReactionProperty> getReactionProperties() {
		return reactionProperties;
	}

	public void setReactionProperties(
			List<CentralDataReactionProperty> reactionProperties) {
		this.reactionProperties = reactionProperties;
	}
	
	

//	public List<CentralDataReactionStoichiometryProperty> getReactionStoichiometryProperties() {
//		return reactionStoichiometryProperties;
//	}
//
//	public void setReactionStoichiometryProperties(
//			List<CentralDataReactionStoichiometryProperty> reactionStoichiometryProperties) {
//		this.reactionStoichiometryProperties = reactionStoichiometryProperties;
//	}

	public List<CentralDataReactionProperty> getReactionStoichiometryProperties() {
		return reactionStoichiometryProperties;
	}

	public void setReactionStoichiometryProperties(
			List<CentralDataReactionProperty> reactionStoichiometryProperties) {
		this.reactionStoichiometryProperties = reactionStoichiometryProperties;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(majorLabel).append(':').append(labels).append('\n');
		sb.append("================   Data   ================\n");
		for (String key : properties.keySet()) {
			sb.append(key).append(':').append(properties.get(key)).append('\n');
		}
		sb.append("================  Stoich  ================\n");
		for (CentralDataReactionProperty stoichiometryProperty : reactionStoichiometryProperties) {
			sb.append(stoichiometryProperty).append('\n');
		}
		sb.append("================Properties================");
		for (CentralDataReactionProperty reactionProperty : reactionProperties) {
			sb.append('\n').append(reactionProperty);
		}
		return sb.toString();
	}
}
