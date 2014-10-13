package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="SEED_COMPOUND_CUE")
public class SeedCompoundCueEntity extends AbstractSeedCue {

	private static final long serialVersionUID = 1L;
	
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private SeedMetaboliteEntity seedCompoundEntity;

	public SeedMetaboliteEntity getSeedCompoundEntity() {
		return seedCompoundEntity;
	}
	public void setSeedCompoundEntity(SeedMetaboliteEntity seedCompoundEntity) {
		this.seedCompoundEntity = seedCompoundEntity;
	}

}
