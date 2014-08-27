package edu.uminho.biosynth.integration.factory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.uminho.biosynth.integration.CentralReactionEntity;

public class CentralReactionFactory {

//	private String entry = "reaction1";
	
	private String majorLabel = "BioDatabase";
	private Set<String> labels = new HashSet<> ();
	private Map<String, Object> properties = new HashMap<> ();
	
	public CentralReactionFactory withEntry(String entry) {
//		this.entry = entry;
		this.properties.put("entry", entry);
		return this;
	}
	
	public CentralReactionFactory withMajorLabel(String majorLabel) {
		this.majorLabel = majorLabel;
		return this;
	}
	
	public CentralReactionFactory withLabels(String[] labels) {
		this.labels.clear();
		this.labels.addAll(Arrays.asList(labels));
		return this;
	}
	
	public CentralReactionFactory withReactant(String entry, Double value, String stoichiometry) {
		
		return this;
	}
	
	public CentralReactionFactory withProduct(String entry, Double value, String stoichiometry) {
		
		return this;
	}
	
	public CentralReactionFactory withCrossreferenceTo(String majorLabel, String entry) {
		return this;
	}
	
	public CentralReactionFactory() {
		labels.add(majorLabel);
		labels.add("Label1");
		labels.add("Label2");
		properties.put("StringValue", "abc");
		properties.put("Integer", 1);
		properties.put("Double", 0.123d);
		properties.put("Array", new String[]{"e1", "e2", "e3"});
	}
	
	public CentralReactionEntity build() {
		CentralReactionEntity entity = new CentralReactionEntity();
		
		entity.setEntry((String)properties.get("entry"));
		entity.setMajorLabel(majorLabel);
		entity.setLabels(labels);
		entity.setProperties(properties);
		
		return entity;
	}
}
