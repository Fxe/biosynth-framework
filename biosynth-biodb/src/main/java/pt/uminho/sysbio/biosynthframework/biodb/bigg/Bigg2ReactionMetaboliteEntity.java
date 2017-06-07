package pt.uminho.sysbio.biosynthframework.biodb.bigg;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.StoichiometryPair;

@Entity
@Table(name="bigg2_reaction_metabolite")
public class Bigg2ReactionMetaboliteEntity extends StoichiometryPair {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="reaction_id")
  private Bigg2ReactionEntity bigg2ReactionEntity;
  
  private String compartment_bigg_id;
  
  private String name;

  public Bigg2ReactionEntity getBigg2ReactionEntity() { return bigg2ReactionEntity;}
  public void setBigg2ReactionEntity(Bigg2ReactionEntity bigg2ReactionEntity) {
    this.bigg2ReactionEntity = bigg2ReactionEntity;
  }

  public String getCompartment_bigg_id() {
    return compartment_bigg_id;
  }

  public void setCompartment_bigg_id(String compartment_bigg_id) {
    this.compartment_bigg_id = compartment_bigg_id;
  }

  public String getName() { return name;}
  public void setName(String name) { this.name = name;}
  
}
