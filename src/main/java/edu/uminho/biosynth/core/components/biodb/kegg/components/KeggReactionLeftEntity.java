package edu.uminho.biosynth.core.components.biodb.kegg.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.StoichiometryPair;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggReactionEntity;

@Entity
@Table(name="kegg_reaction_left")
public class KeggReactionLeftEntity extends StoichiometryPair{

	private static final long serialVersionUID = 1661128047150722207L;

    @Column(name="coefficient") protected String coefficient;
	public String getCoefficient() { return coefficient;}
	public void setCoefficient(String coefficient) { this.coefficient = coefficient;}
	
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private KeggReactionEntity keggReactionEntity;
	public KeggReactionEntity getKeggReactionEntity() { return keggReactionEntity;}
	public void setKeggReactionEntity(KeggReactionEntity keggReactionEntity) { this.keggReactionEntity = keggReactionEntity;}
	
	@Override
	public String toString() {
		return String.format("<%s, %s, %f>", this.cpdEntry, this.coefficient, this.value);
	}
}
