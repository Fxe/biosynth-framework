package pt.uminho.sysbio.biosynthframework.biodb.seed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

@Entity
@Table(name="modelseed_reaction_crossreference")
public class ModelSeedReactionCrossreferenceEntity extends GenericCrossreference {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="reaction_id")
  private ModelSeedReactionEntity modelSeedReactionEntity;
  
  public ModelSeedReactionCrossreferenceEntity() {
    super(null, null, null);
  }
  
  public ModelSeedReactionCrossreferenceEntity(ReferenceType type, String reference, String value) {
    super(type, reference, value);
  }
  
  public ModelSeedReactionCrossreferenceEntity(GenericCrossreference crossreference) {
    super(crossreference.getType(), crossreference.getRef(), crossreference.getValue());
  }
  
  public ModelSeedReactionEntity getModelSeedReactionEntity() {
    return modelSeedReactionEntity;
  }
  
  public void setModelSeedReactionEntity(ModelSeedReactionEntity modelSeedReactionEntity) {
    this.modelSeedReactionEntity = modelSeedReactionEntity;
  }
  
  
}
