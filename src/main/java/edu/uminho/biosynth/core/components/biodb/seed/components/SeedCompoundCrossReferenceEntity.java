package edu.uminho.biosynth.core.components.biodb.seed.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.seed.SeedMetaboliteEntity;

@Entity
@Table(name="SEED_COMPOUND_CROSSREF")
public class SeedCompoundCrossReferenceEntity extends GenericCrossReference {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="ID_COMPOUND")
	private SeedMetaboliteEntity seedCompoundEntity;
	
	public SeedCompoundCrossReferenceEntity() {
		super(null, null, null);
	}
	public SeedCompoundCrossReferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
	public SeedCompoundCrossReferenceEntity(GenericCrossReference crossReference) {
		super(crossReference.getType(), crossReference.getRef(), crossReference.getValue());
	}

	public SeedMetaboliteEntity getSeedCompoundEntity() {
		return seedCompoundEntity;
	}
	public void setSeedCompoundEntity(SeedMetaboliteEntity seedCompoundEntity) {
		this.seedCompoundEntity = seedCompoundEntity;
	}
}
