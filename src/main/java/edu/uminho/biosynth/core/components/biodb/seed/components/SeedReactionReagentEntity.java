package edu.uminho.biosynth.core.components.biodb.seed.components;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.StoichiometryPair;
import edu.uminho.biosynth.core.components.biodb.seed.SeedReactionEntity;

@Entity
@Table(name="SEED_REACTION_REAGENT")
public class SeedReactionReagentEntity extends StoichiometryPair {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name="ID_REACTION")
	private SeedReactionEntity seedReactionEntity;
	
	@Column(name="IS_COFACT") private short isCofactor;
	@Column(name="CPD_UUID") private String compound_uuid;
	@Column(name="CMP_UUID") private String destinationCompartment_uuid;
	@Column(name="IS_TRANS") private short isTransport;
	
	public short getIsCofactor() {
		return isCofactor;
	}
	public void setIsCofactor(short isCofactor) {
		this.isCofactor = isCofactor;
	}
	
	public String getCompound_uuid() {
		return compound_uuid;
	}
	public void setCompound_uuid(String compound_uuid) {
		this.compound_uuid = compound_uuid;
	}
	
	public String getDestinationCompartment_uuid() {
		return destinationCompartment_uuid;
	}
	public void setDestinationCompartment_uuid(String destinationCompartment_uuid) {
		this.destinationCompartment_uuid = destinationCompartment_uuid;
	}
	
	public short getIsTransport() {
		return isTransport;
	}
	public void setIsTransport(short isTransport) {
		this.isTransport = isTransport;
	}
	
	public SeedReactionEntity getSeedReactionEntity() {
		return seedReactionEntity;
	}
	public void setSeedReactionEntity(SeedReactionEntity seedReactionEntity) {
		this.seedReactionEntity = seedReactionEntity;
	}
	
	@Override
	public String toString() {
		final char sep = ',';
		final char ini = '<';
		final char end = '>';
		StringBuilder sb = new StringBuilder();
		sb.append(ini);
		sb.append("isCofactor:").append(isCofactor).append(sep);
		sb.append("compound_uuid:").append(compound_uuid).append(sep);
		sb.append("destinationCompartment_uuid:").append(destinationCompartment_uuid).append(sep);
		sb.append("coefficient:").append(value).append(sep);
		sb.append("isTransport:").append(isTransport);
		sb.append(end);
		return sb.toString();
	}
	
}
