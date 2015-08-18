package pt.uminho.sysbio.biosynthframework.factory;

import pt.uminho.sysbio.biosynthframework.GenericMetabolite;

public class DefaultMetaboliteFactory extends AbstractGenericEntityFactory {
	
	private String formula = "N2C3H4S5P6K7Al8";
	private String source = "Factory";
	private String metaboliteClass = "FactoryMetabolite";
	
	public DefaultMetaboliteFactory() {
		// TODO Auto-generated constructor stub
	}
	
	public DefaultMetaboliteFactory withEntry(String entry) {
		this.entry = entry;
		return this;
	}
	
	public DefaultMetaboliteFactory withName(String name) {
		this.name = name;
		return this;
	}
	
	public DefaultMetaboliteFactory withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public GenericMetabolite build() {
		GenericMetabolite cpd = new GenericMetabolite();
		
		cpd.setId(id);
		cpd.setEntry(entry);
		cpd.setName(name);
		cpd.setDescription(description);
		cpd.setSource(source);
		
		cpd.setFormula(formula);
		cpd.setMetaboliteClass(metaboliteClass);
		
		return cpd;
	}
}
