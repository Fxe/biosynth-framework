package edu.uminho.biosynth.core.components.biodb.biocyc.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycReactionEntity;

@Entity
@Table(name="biocyc_reaction_ec_number")
public class BioCycReactionEcNumberEntity {

	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}
	
	@Column(name="ec_number", nullable=false, length=255)
	private String ecNumber;
	public String getEcNumber() { return ecNumber;}
	public void setEcNumber(String ecNumber) { this.ecNumber = ecNumber;}
	
	@Column(name="official", nullable=true)
	private Boolean official;
	public Boolean getOfficial() { return official;}
	public void setOfficial(Boolean official) { this.official = official;}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private BioCycReactionEntity bioCycReactionEntity;
	public BioCycReactionEntity getBioCycReactionEntity() { return bioCycReactionEntity;}
	public void setBioCycReactionEntity(BioCycReactionEntity bioCycReactionEntity) { this.bioCycReactionEntity = bioCycReactionEntity;}
	
	@Override
	public String toString() {
		return String.format("[%d]<%s, %s>",  id, ecNumber, official);
	}
}
