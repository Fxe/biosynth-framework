package pt.uminho.sysbio.biosynthframework.sbml;

import java.util.ArrayList;
import java.util.List;

public class XMLSbmlMetabolicModel extends XmlObject {
	private List<XmlSbmlSpecie> species = new ArrayList<> ();
	
	public List<XmlSbmlSpecie> getSpecies() {
		return species;
	}
	public void setSpecies(List<XmlSbmlSpecie> species) {
		this.species = species;
	}
	
	
}
