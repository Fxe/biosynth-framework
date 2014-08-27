package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CentralDataMetaboliteProxyEntity {
	
	protected String majorLabel;
	protected Set<String> labels = new HashSet<> ();
	protected Map<String, Object> properties = new HashMap<> ();
	
	public String getMajorLabel() { return majorLabel;}
	public void setMajorLabel(String majorLabel) { this.majorLabel = majorLabel;}
	
	public Set<String> getLabels() { return labels;}
	public void setLabels(Set<String> labels) { this.labels = labels;}
	public void addLabel(String label) { this.labels.add(label);}
	
	public Map<String, Object> getProperties() { return properties;}
	public void setProperties(Map<String, Object> properties) { this.properties = properties;}
	public void putProperty(String key, String value) {
		if (value != null) {
			String trimmed = value.trim();
			if (!trimmed.isEmpty()) {
				properties.put(key, trimmed);
			}
		}
	}
	
	public String getEntry() {
		return this.properties.get("entry").toString();
	}
	
	public void setEntry(String entry) {
		this.properties.put("entry", entry);
	}
}
