package edu.uminho.biosynth.core.components.biodb.seed.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.seed.SeedReactionEntity;

@Entity
@Table(name="SEED_REACTION_CROSSREF")
public class SeedReactionCrossReferenceEntity extends GenericCrossReference{

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_REACTION")
	private SeedReactionEntity seedReactionEntity;
	
	public SeedReactionCrossReferenceEntity() {
		super(null, null, null);
	}
	public SeedReactionCrossReferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
	public SeedReactionCrossReferenceEntity(GenericCrossReference crossReference) {
		super(crossReference.getType(), crossReference.getRef(), crossReference.getValue());
	}

	public SeedReactionEntity getSeedReactionEntity() {
		return seedReactionEntity;
	}
	public void setSeedReactionEntity(SeedReactionEntity seedReactionEntity) {
		this.seedReactionEntity = seedReactionEntity;
	}
}
