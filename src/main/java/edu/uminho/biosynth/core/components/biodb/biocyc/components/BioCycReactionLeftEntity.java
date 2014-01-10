package edu.uminho.biosynth.core.components.biodb.biocyc.components;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.StoichiometryPair;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;

@Entity
@Table(name="BIOCYC_REACTION_LEFT")
public class BioCycReactionLeftEntity extends StoichiometryPair {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="ID_REACTION")
	private BioCycReactionEntity bioCycReactionEntity;
	public BioCycReactionEntity getBioCycReactionEntity() { return bioCycReactionEntity;}
	public void setBioCycReactionEntity(BioCycReactionEntity bioCycReactionEntity) { this.bioCycReactionEntity = bioCycReactionEntity;}
	
}
