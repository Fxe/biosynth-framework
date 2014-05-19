package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A generic property of the unified central data repository
 * 
 * 
 * @author Filipe Liu
 *
 */
public class GenericCentralEntityProperty {
	
	private Long id;
	
	private String majorLabel;
	
	private String uniqueKey;
	
	private Object uniqueKeyValue;
	
	private Set<String> labels = new HashSet<> ();
	
	private Map<String, Object> properties = new HashMap<> ();
	
	private String relationshipMajorLabel;
	
	private Set<String> relationshipLabels = new HashSet<> ();
	
	private Map<String, Object> relationshipProperties = new HashMap<> ();

	
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}

	public String getMajorLabel() { return majorLabel;}
	public void setMajorLabel(String majorLabel) {
		this.majorLabel = majorLabel;
		this.labels.add(majorLabel);
	}
	
	public Set<String> getLabels() { return labels;}
	public void setLabels(Set<String> labels) {this.labels = labels;}
	public void addLabel(String label) { this.labels.add(label);}

	public Map<String, Object> getProperties() { return properties;}
	public void setProperties(Map<String, Object> properties) { this.properties = properties;}

	public String getUniqueKey() { return uniqueKey;}
	public void setUniqueKey(String uniqueKey) { this.uniqueKey = uniqueKey;}

	public Object getUniqueKeyValue() {
		return uniqueKeyValue;
	}

	public void setUniqueKeyValue(Object uniqueKeyValue) {
		this.uniqueKeyValue = uniqueKeyValue;
	}
	
	
	public String getRelationshipMajorLabel() {
		return relationshipMajorLabel;
	}

	public void setRelationshipMajorLabel(String relationshipMajorLabel) {
		this.relationshipMajorLabel = relationshipMajorLabel;
	}

	public Set<String> getRelationshipLabels() {
		return relationshipLabels;
	}

	public void setRelationshipLabels(Set<String> relationshipLabels) {
		this.relationshipLabels = relationshipLabels;
	}
	public void addRelationshipLabel(String label) { this.relationshipLabels.add(label);}

	public Map<String, Object> getRelationshipProperties() {
		return relationshipProperties;
	}

	public void setRelationshipProperties(Map<String, Object> relationshipProperties) {
		this.relationshipProperties = relationshipProperties;
	}

	@Override
	public String toString() {
		return String.format("%s => %s -> %s => %s:%s", relationshipLabels, relationshipProperties, labels, uniqueKey, uniqueKeyValue);
	}
}
