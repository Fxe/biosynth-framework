package edu.uminho.biosynth.core.data.integration.neo4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.components.GenericMetabolite;

public class CentralDataMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = -1644731569591893118L;

	private String majorLabel;
	
	private Set<String> labels = new HashSet<> ();
	
	private Map<String, Object> properties = new HashMap<> ();
	
	private List<CentralDataMetaboliteProxyEntity> crossreferences = new  ArrayList<> ();

	private List<CentralDataMetabolitePropertyEntity> propertyEntities = new ArrayList<> ();
	
	@Override
	public void setEntry(String entry) {
		this.entry = entry;
		properties.put("entry", entry);
	};
	
	@Override
	public void setFormula(String formula) {
		this.formula = formula;
		properties.put("formula", formula);
	};
	
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

	public Map<String, Object> getProperties() { return properties;}
	public void setProperties(Map<String, Object> properties) { this.properties = properties;}
	public void putProperty(String key, String value) {
		String trimmed = value.trim();
		if (!trimmed.isEmpty()) {
			properties.put(key, trimmed);
		}
	}

	
	public List<CentralDataMetaboliteProxyEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(
			List<CentralDataMetaboliteProxyEntity> crossreferences) {
		this.crossreferences = crossreferences;
	}

	public List<CentralDataMetabolitePropertyEntity> getPropertyEntities() {
		return propertyEntities;
	}
	public void setPropertyEntities(
			List<CentralDataMetabolitePropertyEntity> propertyEntities) {
		this.propertyEntities = propertyEntities;
	}
	
	
	
}
