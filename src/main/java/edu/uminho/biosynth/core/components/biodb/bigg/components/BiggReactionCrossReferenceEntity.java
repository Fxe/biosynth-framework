package edu.uminho.biosynth.core.components.biodb.bigg.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uminho.biosynth.core.components.GenericCrossReference;
import edu.uminho.biosynth.core.components.biodb.bigg.BiggReactionEntity;

@Entity
@Table(name="BIGG_REACTION_CROSSREF")
public class BiggReactionCrossReferenceEntity extends GenericCrossReference {
	
	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private BiggReactionEntity biggReactionEntity;
	public BiggReactionEntity getBiggReactionEntity() { return this.biggReactionEntity; }
	public void setBiggReactionEntity(BiggReactionEntity biggReactionEntity) {
		this.biggReactionEntity = biggReactionEntity;
	}
	
	public BiggReactionCrossReferenceEntity() { super(null, null, null); }
	public BiggReactionCrossReferenceEntity(Type type, String reference, String value) {
		super(type, reference, value);
	}
}
