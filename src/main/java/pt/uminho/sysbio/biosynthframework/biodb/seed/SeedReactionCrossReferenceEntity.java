package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossReference;

@Entity
@Table(name="seed_reaction_crossreference")
public class SeedReactionCrossReferenceEntity extends GenericCrossReference{

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
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
