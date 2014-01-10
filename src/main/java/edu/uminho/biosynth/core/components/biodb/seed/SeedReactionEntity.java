package edu.uminho.biosynth.core.components.biodb.seed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedReactionCueEntity;
import edu.uminho.biosynth.core.components.biodb.seed.components.SeedReactionReagentEntity;

@Entity
@Table(name="SEED_REACTION")
public class SeedReactionEntity extends GenericReaction {
    
	private static final long serialVersionUID = 1L;
	
	@Column(name="LOCKED") private short locked;
    @Column(name="DELTAG") private double deltaG;
    @Column(name="DELTAGERR") private double deltaGErr;
    @Column(name="UUID") private String uuid;
    @Column(name="STATUS") private String status;
    @Column(name="CKSUM") private String cksum;
    @Column(name="DEFPROTONS") private int defaultProtons;
    @Column(name="DIRECTION") private String direction;
    @Column(name="ABBREVIATION") private String abbreviation;
    
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="MODDATE") private DateTime modDate;
    
    @OneToMany (mappedBy = "seedReactionEntity")
    private List<SeedReactionReagentEntity> reagents = new ArrayList<> ();
    
    @OneToMany (mappedBy = "seedReactionEntity")
    private List<SeedReactionCueEntity> reactionCues = new ArrayList<> ();
    
    @OneToMany(mappedBy = "seedReactionEntity")
    private List<SeedReactionCrossReferenceEntity> crossReferences = new ArrayList<> ();
    
	public short getLocked() {
		return locked;
	}
	public void setLocked(short locked) {
		this.locked = locked;
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
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

	public String getCksum() {
		return cksum;
	}
	public void setCksum(String cksum) {
		this.cksum = cksum;
	}

	public String getThermoReversibility() {
		String thermoReversibility;
		switch (this.orientation) {
			case 0: 
				thermoReversibility = "<=>";
				break;
			case 1:
				thermoReversibility = "=>";
				break;
			case -2:
				thermoReversibility = "<=";
				break;
			default:
				thermoReversibility = "<=>";
				break;
		}
		return thermoReversibility;
	}
	public void setThermoReversibility(String thermoReversibility) {
		switch (thermoReversibility) {
			case "<=>": 
				this.orientation = 0;
				break;
			case "=>":
				this.orientation = 1;
				break;
			case "<=":
				this.orientation = -1;
				break;
			default:
				this.orientation = 0;
				break;
		}
	}

	public int getDefaultProtons() {
		return defaultProtons;
	}
	public void setDefaultProtons(int defaultProtons) {
		this.defaultProtons = defaultProtons;
	}

	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getAbbreviation() {
		return abbreviation;
	}
	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public DateTime getModDate() {
		return modDate;
	}
	public void setModDate(String modDate) {
		this.modDate = new DateTime(modDate);
	}

	public List<SeedReactionReagentEntity> getReagents() {
		return reagents;
	}
	public void setReagents(List<SeedReactionReagentEntity> reagents) {
		this.reagents = new ArrayList<>(reagents);
		for (SeedReactionReagentEntity reagent : this.reagents) {
			reagent.setSeedReactionEntity(this);
		}
	}
    
	public List<SeedReactionCueEntity> getReactionCues() {
		return reactionCues;
	}
	public void setReactionCues(List<SeedReactionCueEntity> reactionCues) {
		this.reactionCues = new ArrayList<>(reactionCues);
		for (SeedReactionCueEntity reactionCue : this.reactionCues) {
			reactionCue.setSeedReactionEntity(this);
		}
	}
	
	@OneToMany(fetch = FetchType.LAZY)
	public List<SeedReactionCrossReferenceEntity> getCrossReferences() {
		return crossReferences;
	}
	public void setCrossReferences(List<SeedReactionCrossReferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<>(crossReferences);
		for (SeedReactionCrossReferenceEntity xref : this.crossReferences) {
			xref.setSeedReactionEntity(this);
		}
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("locked:").append(locked).append(sep);
		sb.append("deltaG:").append(deltaG).append(sep);
		sb.append("name:").append(name).append(sep);
		sb.append("reagents:").append(reagents).append(sep);
		sb.append("cksum:").append(cksum).append(sep);
		sb.append("uuid:").append(uuid).append(sep);
		sb.append("defaultProtons:").append(defaultProtons).append(sep);
		sb.append("thermoReversibility:").append(this.getThermoReversibility()).append(sep);
		sb.append("direction:").append(direction).append(sep);
		sb.append("modDate:").append(modDate).append(sep);
		sb.append("abbreviation:").append(abbreviation).append(sep);
		sb.append("reactionCues:").append(reactionCues).append(sep);
		sb.append("deltaGErr:").append(deltaGErr).append(sep);
		return sb.toString();
	}
}
