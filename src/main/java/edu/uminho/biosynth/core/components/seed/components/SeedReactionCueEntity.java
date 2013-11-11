package edu.uminho.biosynth.core.components.seed.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.seed.SeedReactionEntity;

@Entity
@Table(name="SEED_REACTION_CUE")
public class SeedReactionCueEntity extends AbstractSeedCue{
	
	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="ID_REACTION")
	private SeedReactionEntity seedReactionEntity;

	public SeedReactionEntity getSeedReactionEntity() {
		return seedReactionEntity;
	}
	public void setSeedReactionEntity(SeedReactionEntity seedReactionEntity) {
		this.seedReactionEntity = seedReactionEntity;
	}
}
