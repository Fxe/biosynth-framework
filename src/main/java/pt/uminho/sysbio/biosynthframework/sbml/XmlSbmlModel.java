package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlSbmlModel {
	
	public Map<String, String> attributes = new HashMap<> ();
	public Map<String, String> getAttributes() { return attributes;}
	public void setAttributes(Map<String, String> attributes) { this.attributes = attributes;}
	
	private List<XmlSbmlSpecie> species = new ArrayList<> ();
	private List<XmlSbmlGroup> groups = new ArrayList<> ();
	private List<XmlObject> fluxBounds = new ArrayList<>();
	
	public List<XmlSbmlSpecie> getSpecies() {
		return species;
	}
	public void setSpecies(List<XmlSbmlSpecie> species) {
		this.species = species;
	}
	public List<XmlSbmlGroup> getGroups() {
		return groups;
	}
	public void setGroups(List<XmlSbmlGroup> groups) {
		this.groups = groups;
	}
	public List<XmlObject> getFluxBounds() {
		return fluxBounds;
	}
	public void setFluxBounds(List<XmlObject> fluxBounds) {
		this.fluxBounds = fluxBounds;
	}
	
	
}
