package edu.uminho.biosynth.core.components.biodb.seed.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.biodb.seed.SeedMetaboliteEntity;

@Entity
@Table(name="SEED_COMPOUND_CUE")
public class SeedCompoundCueEntity extends AbstractSeedCue {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_COMPOUND")
	private SeedMetaboliteEntity seedCompoundEntity;

	public SeedMetaboliteEntity getSeedCompoundEntity() {
		return seedCompoundEntity;
	}
	public void setSeedCompoundEntity(SeedMetaboliteEntity seedCompoundEntity) {
		this.seedCompoundEntity = seedCompoundEntity;
	}

}
