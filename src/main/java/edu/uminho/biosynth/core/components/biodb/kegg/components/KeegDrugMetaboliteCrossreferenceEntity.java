package edu.uminho.biosynth.core.components.biodb.kegg.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggDrugMetaboliteEntity;

@Entity
@Table(name="kegg_drug_metabolite_crossreference")
public class KeegDrugMetaboliteCrossreferenceEntity extends GenericCrossReference{

	private static final long serialVersionUID = 4269987711164363768L;
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private KeggDrugMetaboliteEntity keggDrugMetaboliteEntity;
	public KeggDrugMetaboliteEntity getKeggDrugMetaboliteEntity() { return keggDrugMetaboliteEntity; }
	public void setKeggDrugMetaboliteEntity(KeggDrugMetaboliteEntity keggDrugMetaboliteEntity) {
		this.keggDrugMetaboliteEntity = keggDrugMetaboliteEntity;
	}
	
	public KeegDrugMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public KeegDrugMetaboliteCrossreferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
}
