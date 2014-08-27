package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.components.GenericReaction;

public class CentralReactionEntity extends GenericReaction {

	private static final long serialVersionUID = 3591210100735283939L;

	private String majorLabel;
	private Set<String> labels = new HashSet<> ();
	private Map<String, Object> properties = new HashMap<> ();
	
	private Map<CentralMetaboliteEntity, Double> reactants = new HashMap<> ();
	private Map<CentralMetaboliteEntity, Double> products = new HashMap<> ();
	
	public String getMajorLabel() { return majorLabel;}
	public void setMajorLabel(String majorLabel) { this.majorLabel = majorLabel;}

	public Set<String> getLabels() { return labels;}
	public void setLabels(Set<String> labels) { this.labels = labels;}
	
	public Map<String, Object> getProperties() { return properties;}
	public void setProperties(Map<String, Object> properties) { this.properties = properties;}
	public void putProperty(String key, String value) {
		String trimmed = value.trim();
		if (!trimmed.isEmpty()) {
			properties.put(key, trimmed);
		}
	}
	
	@Override
	public void setEntry(String entry) {
		this.entry = entry;
		properties.put("entry", entry);
	};
	
	@Override
	public void setReactantStoichiometry(Map<String,Double> reactantStoichiometry) {
		super.setReactantStoichiometry(reactantStoichiometry);
	};
	
	@Override
	public void setProductStoichiometry(Map<String, Double> productStoichiometry) {
		super.setProductStoichiometry(productStoichiometry);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(super.toString()).append("\n");
		sb.append("MajorLabel: ").append(this.majorLabel).append("\n");
		sb.append("Labels: ").append(this.labels).append("\n");
		sb.append("Properties:\n");
		for (String key : this.properties.keySet())
			sb.append(String.format("\t%s: %s\n", key, this.properties.get(key)));
		return sb.toString();
	}
}
