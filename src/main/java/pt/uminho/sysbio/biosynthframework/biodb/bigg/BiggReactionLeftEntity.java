package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.StoichiometryPair;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="bigg_reaction_left")
public class BiggReactionLeftEntity extends StoichiometryPair {

	private static final long serialVersionUID = 1L;
	
	@Column(name="compartment", nullable=false, length=7)
	private String compartment;
	public String getCompartment() { return compartment;}
	public void setCompartment(String compartment) { this.compartment = compartment;}
	
	@Column(name="comment", nullable=true, length=255)
	private String comment;
	public String getComment() { return comment;}
	public void setComment(String comment) { this.comment = comment;}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private BiggReactionEntity biggReactionEntity;
	public BiggReactionEntity getBiggReactionEntity() { return biggReactionEntity;}
	public void setBiggReactionEntity(BiggReactionEntity biggReactionEntity) { this.biggReactionEntity = biggReactionEntity;}
	
	@Override
	public String toString() {
		return String.format("<%s,%s,%s>", this.cpdEntry, this.value, this.compartment);
	}
}
