package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.StoichiometryPair;

@Entity
@Table(name="seed_reaction_reagent")
public class SeedReactionReagentEntity extends StoichiometryPair {

	private static final long serialVersionUID = 1L;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private SeedReactionEntity seedReactionEntity;
	public SeedReactionEntity getSeedReactionEntity() { return seedReactionEntity;}
	public void setSeedReactionEntity(SeedReactionEntity seedReactionEntity) { this.seedReactionEntity = seedReactionEntity;}
	
	@Column(name="is_cofactor") private short isCofactor;
	public short getIsCofactor() { return isCofactor;}
	public void setIsCofactor(short isCofactor) { this.isCofactor = isCofactor;}
	
	@Column(name="cpd_uuid") private String compound_uuid;
	public String getCompound_uuid() { return compound_uuid;}
	public void setCompound_uuid(String compound_uuid) { this.compound_uuid = compound_uuid;}
	
	@Column(name="cmp_uuid") private String destinationCompartment_uuid;
	public String getDestinationCompartment_uuid() { return destinationCompartment_uuid;}
	public void setDestinationCompartment_uuid(String destinationCompartment_uuid) { this.destinationCompartment_uuid = destinationCompartment_uuid;}
	
	@Column(name="cmp_entry") private String cmpEntry;
	public String getCmpEntry() { return cmpEntry;}
	public void setCmpEntry(String cmpEntry) { this.cmpEntry = cmpEntry;}
	
	@Column(name="cmp_name") private String cmpName;
	public String getCmpName() { return cmpName;}
	public void setCmpName(String cmpName) { this.cmpName = cmpName;}

	@Column(name="is_transport") private short isTransport;
	public short getIsTransport() { return isTransport;}
	public void setIsTransport(short isTransport) { this.isTransport = isTransport;}
	
	@Column(name="coefficient") private short coefficient;
	public short getCoefficient() { return coefficient;}
	public void setCoefficient(short coefficient) { this.coefficient = coefficient;}
	
	@Override
	public String toString() {
		return String.format("<[%s]%.1f %s>", cmpEntry, stoichiometry, cpdEntry);
//		final char sep = ',';
//		final char ini = '<';
//		final char end = '>';
//		StringBuilder sb = new StringBuilder();
//		sb.append(ini);
//		sb.append("isCofactor:").append(isCofactor).append(sep);
//		sb.append("compound_uuid:").append(compound_uuid).append(sep);
//		sb.append("destinationCompartment_uuid:").append(destinationCompartment_uuid).append(sep);
//		sb.append("coefficient:").append(coefficient).append(sep);
//		sb.append("isTransport:").append(isTransport);
//		sb.append(end);
//		return sb.toString();
	}
	
}
