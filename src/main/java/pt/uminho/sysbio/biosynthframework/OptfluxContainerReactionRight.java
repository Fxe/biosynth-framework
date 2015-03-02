package pt.uminho.sysbio.biosynthframework;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="optflux_container_reaction_right")
public class OptfluxContainerReactionRight extends StoichiometryPair {

	private static final long serialVersionUID = 1L;

	@MetaProperty
    @Column(name="compartment") protected String compartment;
	public String getCompartment() { return compartment;}
	public void setCompartment(String compartment) { this.compartment = compartment;}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private OptfluxContainerReactionEntity reaction;
	public OptfluxContainerReactionEntity getReaction() { return reaction;}
	public void setReaction(OptfluxContainerReactionEntity reaction) { this.reaction = reaction;}
	
	@Override
	public String toString() {
		return String.format("<%s, %.2f, %s>", this.cpdEntry, this.stoichiometry, this.compartment);
	}
}
