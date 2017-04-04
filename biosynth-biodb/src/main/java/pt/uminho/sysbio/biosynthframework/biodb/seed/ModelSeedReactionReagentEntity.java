package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.StoichiometryPair;

@Entity
@Table(name="modelseed_reaction_reagent")
public class ModelSeedReactionReagentEntity extends StoichiometryPair {

  private static final long serialVersionUID = 1L;
  
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="reaction_id")
  private ModelSeedReactionEntity modelSeedReactionEntity;
  
  @Column(name="compartment") 
  protected Integer compartment;

  public ModelSeedReactionEntity getModelSeedReactionEntity() {
    return modelSeedReactionEntity;
  }

  public void setModelSeedReactionEntity(ModelSeedReactionEntity modelSeedReactionEntity) {
    this.modelSeedReactionEntity = modelSeedReactionEntity;
  }

  public Integer getCompartment() { return compartment;}
  public void setCompartment(Integer compartment) { this.compartment = compartment;}
  
  
}
