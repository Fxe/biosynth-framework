package pt.uminho.sysbio.biosynth.integration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AbstractCentralEntity {

	protected Long id;
	protected String majorLabel;
	
	protected Set<String> labels = new HashSet<> ();
	protected Map<String, Object> properties = new HashMap<> ();
	
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
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
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("id:%d\n", id));
		sb.append(String.format("majorLabel:%s\n", majorLabel));
		sb.append(String.format("labels:%s\n", labels));
		sb.append("Self Properties:\n");
		for (String key : properties.keySet()) {
			sb.append(String.format("\t%s:%s\n", key, properties.get(key)));
		}
		return sb.toString();
	}
}
