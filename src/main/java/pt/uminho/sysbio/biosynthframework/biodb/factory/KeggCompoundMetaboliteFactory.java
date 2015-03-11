package pt.uminho.sysbio.biosynthframework.biodb.factory;

import java.util.ArrayList;
import java.util.List;

import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggCompoundMetaboliteEntity;

public class KeggCompoundMetaboliteFactory {

	private final String entry;
	private Long id;
	private String name;
	private String formula;
	private String description;
	private List<KeggCompoundMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
	
	public KeggCompoundMetaboliteFactory(String entry) {
		this.entry = entry;
	}
	
	public KeggCompoundMetaboliteFactory withId(Long id) {
		this.id = id;
		return this;
	}
	
	public KeggCompoundMetaboliteFactory withName(String name) {
		this.name = name;
		return this;
	}
	
	public KeggCompoundMetaboliteFactory withFormula(String formula) {
		this.formula = formula;
		return this;
	}
	
	public KeggCompoundMetaboliteFactory withDescription(String description) {
		this.description = description;
		return this;
	}
	
	public KeggCompoundMetaboliteFactory withCrossreference(String tag, String entry) {
		KeggCompoundMetaboliteCrossreferenceEntity crossreferenceEntity =
				new KeggCompoundMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, tag, entry);
		this.crossReferences.add(crossreferenceEntity);
		return this;
	}
	
	public KeggCompoundMetaboliteEntity build() {
		KeggCompoundMetaboliteEntity entity = new KeggCompoundMetaboliteEntity();
		
		entity.setId(id);
		entity.setEntry(entry);
		entity.setName(name);		
		entity.setFormula(formula);
		entity.setDescription(description);
		entity.setCrossReferences(crossReferences);
		
		return entity;
	}
}
