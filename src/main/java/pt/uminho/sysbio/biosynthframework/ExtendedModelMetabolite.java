package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExtendedModelMetabolite extends GenericMetabolite implements PropertyContainer {

	private static final long serialVersionUID = 1L;
	
	private ExtendedMetabolicModelEntity metabolicModel;
	public ExtendedMetabolicModelEntity getMetabolicModel() { return metabolicModel;}
	public void setMetabolicModel(
			ExtendedMetabolicModelEntity metabolicModel) {
		this.metabolicModel = metabolicModel;
	}

	private List<ExtendedMetaboliteSpecie> species = new ArrayList<> ();
	public List<ExtendedMetaboliteSpecie> getSpecies() { return species;}
	public void setSpecies(List<ExtendedMetaboliteSpecie> species) {
		this.species = species;
	}
	
	private Map<String, Object> properties = new HashMap<> ();
	
	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}
	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
		if (properties.containsKey("entry")) {
			this.entry = ((String) properties.get("entry")).split("@")[0];
		}
		if (properties.containsKey("source")) {
			this.source = (String) properties.get("source");
		}
		if (properties.containsKey("description")) {
			this.description = (String) properties.get("description");
		}
	}
	
	
	private List<DefaultMetaboliteSpecieReference> crossreferences = new ArrayList<> ();
	public List<DefaultMetaboliteSpecieReference> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(
			List<DefaultMetaboliteSpecieReference> crossreferences) {
		this.crossreferences = crossreferences;
	}
}
