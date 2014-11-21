package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.StoichiometryPair;

@Entity
@Table(name="kegg_reaction_right")
public class KeggReactionRightEntity extends StoichiometryPair{

	private static final long serialVersionUID = 2200789460752514142L;

    @Column(name="coefficient") protected String coefficient;
	public String getCoefficient() { return coefficient;}
	public void setCoefficient(String coefficient) { this.coefficient = coefficient;}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="reaction_id")
	private KeggReactionEntity keggReactionEntity;
	public KeggReactionEntity getKeggReactionEntity() { return keggReactionEntity;}
	public void setKeggReactionEntity(KeggReactionEntity keggReactionEntity) { this.keggReactionEntity = keggReactionEntity;}
	
	@Override
	public String toString() {
		return String.format("<%s, %s, %f>", this.cpdEntry, this.coefficient, this.stoichiometry);
	}
	
}
