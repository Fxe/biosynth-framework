package edu.uminho.biosynth.core.components.biodb.kegg.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;

@Entity
@Table(name="kegg_compound_metabolite_crossreference")
public class KeggCompoundMetaboliteCrossreferenceEntity extends GenericCrossReference {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private KeggCompoundMetaboliteEntity keggMetaboliteEntity;
	public KeggCompoundMetaboliteEntity getKeggMetaboliteEntity() { return keggMetaboliteEntity; }
	public void setKeggMetaboliteEntity(KeggCompoundMetaboliteEntity keggMetaboliteEntity) {
		this.keggMetaboliteEntity = keggMetaboliteEntity;
	}
	
	public KeggCompoundMetaboliteCrossreferenceEntity() { super(null, null, null); }
	public KeggCompoundMetaboliteCrossreferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
	

}
