package pt.uminho.sysbio.biosynth.integration.factory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import pt.uminho.sysbio.biosynth.integration.CentralMetaboliteEntity;

public class CentralMetaboliteFactory {
	
	private String entry;
	
	private String majorLabel = "BioDatabase";
	private Set<String> labels = new HashSet<> ();
	private Map<String, Object> properties = new HashMap<> ();
	
	public CentralMetaboliteFactory withEntry(String entry) {
		this.entry = entry;
		return this;
	}
	
	public CentralMetaboliteFactory() {
		labels.add(majorLabel);
		labels.add("Label1");
		labels.add("Label2");
		properties.put("StringValue", "abc");
		properties.put("Integer", 1);
		properties.put("Double", 0.123d);
		properties.put("Array", new String[]{"e1", "e2", "e3"});
	}
	
	public CentralMetaboliteFactory withLabel(String label) {
		this.labels.add(label);
		return this;
	}
	
	public CentralMetaboliteFactory withMajorLabel(String label) {
		this.majorLabel = label;
		if (!this.labels.contains(label)) this.labels.add(label);
		return this;
	}
	
	public CentralMetaboliteFactory withProperty(String key, Object value) {
		this.properties.put(key, value);
		return this;
	}
	
	public CentralMetaboliteEntity build() {
		CentralMetaboliteEntity entity = new CentralMetaboliteEntity();
		
		entity.setEntry(entry);
		entity.setMajorLabel(majorLabel);
		entity.setLabels(labels);
		entity.setProperties(properties);
		
		return entity;
	}
}
