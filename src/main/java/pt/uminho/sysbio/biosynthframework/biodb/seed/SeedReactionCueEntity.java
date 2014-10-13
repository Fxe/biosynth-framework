package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
