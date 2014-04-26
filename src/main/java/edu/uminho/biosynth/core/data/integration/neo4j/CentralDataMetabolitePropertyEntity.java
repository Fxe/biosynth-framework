package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CentralDataMetabolitePropertyEntity {

	private Long id;
	
	private String majorLabel;
	
	private String uniqueKey;
	
	private Set<String> labels = new HashSet<> ();
	
	private String relationshipType;
	
	private Map<String, Object> properties = new HashMap<> ();

	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMajorLabel() {
		return majorLabel;
	}

	public void setMajorLabel(String majorLabel) {
		this.majorLabel = majorLabel;
		this.labels.add(majorLabel);
	}

	public Set<String> getLabels() {
		return labels;
	}

	public void setLabels(Set<String> labels) {
		this.labels = labels;
	}

	public String getRelationshipType() {
		return relationshipType;
	}

	public void setRelationshipType(String relationshipType) {
		this.relationshipType = relationshipType;
	}

	public Map<String, Object> getProperties() {
		return properties;
	}

	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	public String getUniqueKey() {
		return uniqueKey;
	}

	public void setUniqueKey(String uniqueKey) {
		this.uniqueKey = uniqueKey;
	}
	
	
}
