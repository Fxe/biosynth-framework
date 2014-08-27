package edu.uminho.biosynth.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataMetabolitePropertyEntity;
import edu.uminho.biosynth.core.data.integration.neo4j.CentralDataMetaboliteProxyEntity;

public class CentralMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = -1644731569591893118L;

	private List<CentralDataMetaboliteProxyEntity> crossreferences = new  ArrayList<> ();
	private List<CentralDataMetabolitePropertyEntity> propertyEntities = new ArrayList<> ();
	
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
	
	public List<CentralDataMetaboliteProxyEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(
			List<CentralDataMetaboliteProxyEntity> crossreferences) {
		this.crossreferences = crossreferences;
	}
	public void addCrossreference(CentralDataMetaboliteProxyEntity crossreference) {
		this.crossreferences.add(crossreference);
	}

	public List<CentralDataMetabolitePropertyEntity> getPropertyEntities() {
		return propertyEntities;
	}
	public void setPropertyEntities(
			List<CentralDataMetabolitePropertyEntity> propertyEntities) {
		this.propertyEntities = propertyEntities;
	}
	
	
	
}
