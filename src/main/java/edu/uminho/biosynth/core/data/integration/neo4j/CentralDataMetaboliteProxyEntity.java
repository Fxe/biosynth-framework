package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CentralDataMetaboliteProxyEntity {
	
	private String majorLabel;
	private Set<String> labels = new HashSet<> ();
	private Map<String, Object> properties = new HashMap<> ();
	
	public String getEntry() {
		return this.properties.get("entry").toString();
	}
	
	public void setEntry(String entry) {
		this.properties.put("entry", entry);
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
	
	
}
