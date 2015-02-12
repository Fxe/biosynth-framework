package pt.uminho.sysbio.biosynthframework.biodb.seed;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import pt.uminho.sysbio.biosynthframework.GenericReaction;
import pt.uminho.sysbio.biosynthframework.Orientation;

@Entity
@Table(name="seed_reaction")
public class SeedReactionEntity extends GenericReaction {
    
	private static final long serialVersionUID = 1L;
	
	@Column(name="locked") private short locked;
    @Column(name="deltag") private double deltaG;
    @Column(name="deltagerr") private double deltaGErr;
    @Column(name="uuid") private String uuid;
    @Column(name="status") private String status;
    @Column(name="cksum") private String cksum;
    @Column(name="default_protons") private int defaultProtons;
    @Column(name="DIRECTION") private String direction;
    @Column(name="ABBREVIATION") private String abbreviation;
    
    @Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @Column(name="MODDATE") private DateTime modDate;
    
    @OneToMany (mappedBy = "seedReactionEntity", cascade = CascadeType.ALL)
    private List<SeedReactionReagentEntity> reagents = new ArrayList<> ();
    
    @OneToMany (mappedBy = "seedReactionEntity", cascade = CascadeType.ALL)
    private List<SeedReactionCueEntity> reactionCues = new ArrayList<> ();
    
    @OneToMany(mappedBy = "seedReactionEntity", cascade = CascadeType.ALL)
    private List<SeedReactionCrossreferenceEntity> crossreferences = new ArrayList<> ();
    
	@ElementCollection
	@CollectionTable(name="seed_reaction_synonym", joinColumns=@JoinColumn(name="reaction_id"))
	@Column(name="synonym", length=255)
	private List<String> synonyms = new ArrayList<> ();
	public List<String> getSynonyms() { return synonyms;}
	public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms;}
	
	@ElementCollection
	@CollectionTable(name="seed_reaction_enzyme_class", joinColumns=@JoinColumn(name="reaction_id"))
	@Column(name="enzyme_class", length=255)
	private List<String> enzymeClass = new ArrayList<> ();
	public List<String> getEnzymeClass() { return enzymeClass;}
	public void setEnzymeClass(List<String> enzymeClass) { this.enzymeClass = enzymeClass;}
	
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
			case Reversible: 
				thermoReversibility = "<=>";
				break;
			case LeftToRight:
				thermoReversibility = "=>";
				break;
			case RightToLeft:
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
				this.orientation = Orientation.Reversible;
				break;
			case "=>":
				this.orientation = Orientation.LeftToRight;
				break;
			case "<=":
				this.orientation = Orientation.RightToLeft;
				break;
			default:
				this.orientation = Orientation.Reversible;
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
	public List<SeedReactionCrossreferenceEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossReferences(List<SeedReactionCrossreferenceEntity> crossreferences) {
		this.crossreferences = new ArrayList<>(crossreferences);
		for (SeedReactionCrossreferenceEntity xref : this.crossreferences) {
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
