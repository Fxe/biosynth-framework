package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

@Entity
@Table(name="seed_reaction_crossreference")
public class SeedReactionCrossreferenceEntity extends GenericCrossReference{

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private SeedReactionEntity seedReactionEntity;
	
	public SeedReactionCrossreferenceEntity() {
		super(null, null, null);
	}
	public SeedReactionCrossreferenceEntity(ReferenceType type, String reference, String value) {
		super(type, reference, value);
	}
	public SeedReactionCrossreferenceEntity(GenericCrossReference crossreference) {
		super(crossreference.getType(), crossreference.getRef(), crossreference.getValue());
	}

	public SeedReactionEntity getSeedReactionEntity() {
		return seedReactionEntity;
	}
	public void setSeedReactionEntity(SeedReactionEntity seedReactionEntity) {
		this.seedReactionEntity = seedReactionEntity;
	}
}
