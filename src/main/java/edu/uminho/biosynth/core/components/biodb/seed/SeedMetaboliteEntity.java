package edu.uminho.biosynth.core.components.biodb.seed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedCompoundCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedCompoundCueEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedCompoundPkEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedCompoundStructureEntity;

@Entity
@Table(name="SEED_METABOLITE")
public class SeedMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = 1L;
	
	@Column(name="DEFAULTCHARGE") private Integer defaultCharge;
    @Column(name="DELTAG") private Double deltaG;
    @Column(name="DELTAGERR") private Double deltaGErr;
    @Column(name="UUID") private String uuid;
    @Column(name="CKSUM") private String cksum;
    @Column(name="LOCKED") private short locked;
    @Column(name="MASS") private int mass;
    @Column(name="ABBREVIATION") private String abbreviation;
    @Column(name="UNCHARGEDFORMULA") private String unchargedFormula;
    
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="MODDATE") private DateTime modDate;
	
    @OneToMany(mappedBy = "seedCompoundEntity", cascade = CascadeType.ALL)
    private List<SeedCompoundStructureEntity> structures = new ArrayList<>();
    
    @OneToMany(mappedBy = "seedCompoundEntity", cascade = CascadeType.ALL)
    private List<SeedCompoundPkEntity> pks = new ArrayList<>();
    
    @OneToMany(mappedBy = "seedCompoundEntity", cascade = CascadeType.ALL)
    private List<SeedCompoundCueEntity> compoundCues = new ArrayList<>();
    
    @OneToMany(mappedBy = "seedCompoundEntity", cascade = CascadeType.ALL)
    private List<SeedCompoundCrossReferenceEntity> crossReferences = new ArrayList<>();
	
	public int getDefaultCharge() {
		return defaultCharge;
	}
	public void setDefaultCharge(int defaultCharge) {
		this.defaultCharge = defaultCharge;
	}
	
	public double getDeltaG() {
		return deltaG;
	}
	public void setDeltaG(double deltaG) {
		this.deltaG = deltaG;
	}

	public double getDeltaGErr() {
		return deltaGErr;
	}
	public void setDeltaGErr(double deltaGErr) {
		this.deltaGErr = deltaGErr;
	}

	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getCksum() {
		return cksum;
	}
	public void setCksum(String cksum) {
		this.cksum = cksum;
	}

	public short getLocked() {
		return locked;
	}
	public void setLocked(short locked) {
		this.locked = locked;
	}

	public int getMass() {
		return mass;
	}
	public void setMass(int mass) {
		this.mass = mass;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public String getUnchargedFormula() {
		return unchargedFormula;
	}
	public void setUnchargedFormula(String unchargedFormula) {
		this.unchargedFormula = unchargedFormula;
	}

	public List<SeedCompoundStructureEntity> getStructures() {
		return structures;
	}
	public void setStructures(List<SeedCompoundStructureEntity> structures) {
		this.structures = new ArrayList<> (structures);
		for (SeedCompoundStructureEntity struct : this.structures) {
			struct.setSeedCompoundEntity(this);
		}
	}
	
	public List<SeedCompoundPkEntity> getPks() {
		return pks;
	}
	public void setPks(List<SeedCompoundPkEntity> pks) {
		this.pks = new ArrayList<> (pks);
		for (SeedCompoundPkEntity pk : this.pks) {
			pk.setSeedCompoundEntity(this);
		}
	}
	
	public List<SeedCompoundCueEntity> getCompoundCues() {
		return compoundCues;
	}
	public void setCompoundCues(List<SeedCompoundCueEntity> compoundCues) {
		this.compoundCues = new ArrayList<> (compoundCues);
		for (SeedCompoundCueEntity compoundCue : this.compoundCues) {
			compoundCue.setSeedCompoundEntity(this);
		}
	}
	
	public List<SeedCompoundCrossReferenceEntity> getCrossReferences() {
		return crossReferences;
	}
	public void setCrossReferences(List<SeedCompoundCrossReferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<> (crossReferences);
		for (SeedCompoundCrossReferenceEntity crossReference : this.crossReferences) {
			crossReference.setSeedCompoundEntity(this);
		}
	}
	
	public DateTime getModDate() {
		return modDate;
	}
	public void setModDate(String modDate) {
		this.modDate = new DateTime(modDate);
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("defaultCharge:").append(defaultCharge).append(sep);
		sb.append("locked:").append(locked).append(sep);
		sb.append("deltaG:").append(deltaG).append(sep);
		sb.append("name:").append(name).append(sep);
		sb.append("cksum:").append(cksum).append(sep);
		sb.append("uuid:").append(uuid).append(sep);
		sb.append("structures:").append(structures).append(sep);
		sb.append("pks:").append(pks).append(sep);
		sb.append("formula:").append(formula).append(sep);
		sb.append("mass:").append(mass).append(sep);
		sb.append("modDate:").append(modDate).append(sep);
		sb.append("abbreviation:").append(abbreviation).append(sep);
		sb.append("unchargedFormula:").append(unchargedFormula).append(sep);
		sb.append("compoundCues:").append(compoundCues).append(sep);
		sb.append("deltaGErr:").append(deltaGErr).append(sep);
		return sb.toString();
	}
}
