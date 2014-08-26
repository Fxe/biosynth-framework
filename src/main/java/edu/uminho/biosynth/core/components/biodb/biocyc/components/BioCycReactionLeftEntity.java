package edu.uminho.biosynth.core.components.biodb.biocyc.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uminho.biosynth.core.components.StoichiometryPair;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;

@Entity
@Table(name="biocyc_reaction_left")
public class BioCycReactionLeftEntity extends StoichiometryPair {

	private static final long serialVersionUID = 1L;

    @Column(name="coefficient_str") protected String coefficient;
	public String getCoefficient() { return coefficient;}
	public void setCoefficient(String coefficient) { this.coefficient = coefficient;}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private BioCycReactionEntity bioCycReactionEntity;
	public BioCycReactionEntity getBioCycReactionEntity() { return bioCycReactionEntity;}
	public void setBioCycReactionEntity(BioCycReactionEntity bioCycReactionEntity) { this.bioCycReactionEntity = bioCycReactionEntity;}
	
	@Override
	public String toString() {
		return String.format("<%s, %s, %f>", this.cpdEntry, this.coefficient, this.value);
	}
}
