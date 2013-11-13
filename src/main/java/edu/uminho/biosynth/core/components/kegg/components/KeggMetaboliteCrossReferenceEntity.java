package edu.uminho.biosynth.core.components.kegg.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.kegg.KeggMetaboliteEntity;

@Entity
@Table(name="KEGG_METABOLITE_CROSSREF")
public class KeggMetaboliteCrossReferenceEntity extends GenericCrossReference {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_METABOLITE")
	private KeggMetaboliteEntity keggMetaboliteEntity;
	
	public KeggMetaboliteCrossReferenceEntity() {
		super(null, null, null);
	}
	public KeggMetaboliteCrossReferenceEntity(Type type, String reference,
			String value) {
		super(type, reference, value);
	}
	
	public KeggMetaboliteEntity getKeggMetaboliteEntity() {
		return keggMetaboliteEntity;
	}
	public void setKeggMetaboliteEntity(KeggMetaboliteEntity keggMetaboliteEntity) {
		this.keggMetaboliteEntity = keggMetaboliteEntity;
	}
}
