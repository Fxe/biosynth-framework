package edu.uminho.biosynth.core.components.biodb.bigg.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;

@Entity
@Table(name="BIGG_METABOLITE_CROSSREF")
public class BiggMetaboliteCrossReferenceEntity extends GenericCrossReference {
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_METABOLITE")
	private BiggMetaboliteEntity biggMetaboliteEntity;
	public BiggMetaboliteEntity getBiggMetaboliteEntity() { return this.biggMetaboliteEntity; }
	public void setBiggMetaboliteEntity(BiggMetaboliteEntity biggMetaboliteEntity) {
		this.biggMetaboliteEntity = biggMetaboliteEntity;
	}
	
	public BiggMetaboliteCrossReferenceEntity() { super(null, null, null); }
	public BiggMetaboliteCrossReferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
}
