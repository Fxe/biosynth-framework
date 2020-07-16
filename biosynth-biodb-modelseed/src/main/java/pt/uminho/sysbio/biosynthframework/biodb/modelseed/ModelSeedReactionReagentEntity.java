package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.StoichiometryPair;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="modelseed_reaction_reagent")
public class ModelSeedReactionReagentEntity extends StoichiometryPair {

  private static final long serialVersionUID = 1L;
  
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="reaction_id")
  private ModelSeedReactionEntity modelSeedReactionEntity;
  
  @MetaProperty
  @Column(name="compartment")
  private int compartment;
  
  @MetaProperty
  @Column(name="coefficient") 
  private double coefficient;


  public ModelSeedReactionEntity getModelSeedReactionEntity() {
    return modelSeedReactionEntity;
  }

  public void setModelSeedReactionEntity(ModelSeedReactionEntity modelSeedReactionEntity) {
    this.modelSeedReactionEntity = modelSeedReactionEntity;
  }
  
  public double getCoefficient() { return coefficient;}
  public void setCoefficient(double coefficient) { this.coefficient = coefficient;}

  public int getCompartment() { return compartment;}
  public void setCompartment(int compartment) { this.compartment = compartment;}
  
  
}
