package edu.uminho.biosynth.core.components;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GenericEnzyme extends AbstractGenericEntity implements Serializable {

	public static int TOSTRING_DETAIL = 0;
	
	private static final long serialVersionUID = 454657568009L;

	private Map<String, String> organismGeneMap;
	private String source;
	
	/*
	 * Here Enzymes maps to genes Map<Gene, Organism>
	 */
	
	public GenericEnzyme(String id) {
		super(id);
		this.id = 0;
		this.name = "unnamed";
		this.organismGeneMap = new HashMap<String, String> ();
	}
	
	public GenericEnzyme(String id, int key) {
		super(id);
		this.id = key;
		this.name = "unnamed";
		this.organismGeneMap = new HashMap<String, String> ();
	}
	
	public void addOrganimsMap( Map<String, String> orgGeneMap) {
		if ( orgGeneMap != null) this.organismGeneMap.putAll(orgGeneMap);
	}
	
	public void addOrganims(String orgId, String gene) {
		this.organismGeneMap.put(gene, orgId);
	}
	
	public Map<String, String> getOrganismMap() {
		return this.organismGeneMap;
	}
	
	public Set<String> getOrganimsIds() {
		return this.organismGeneMap.keySet();
	}
	
	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
//		sb.append(" - ").append('#').append( this.getOrganimsIDs().size()).append( this.getOrganimsIDs());
		sb.append(" - ").append('#').append( this.organismGeneMap);
		return sb.toString();
	}
}
