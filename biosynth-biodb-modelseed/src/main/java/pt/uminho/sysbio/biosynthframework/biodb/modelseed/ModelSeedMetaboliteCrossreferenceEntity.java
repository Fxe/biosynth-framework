package pt.uminho.sysbio.biosynthframework.biodb.modelseed;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericCrossreference;
import pt.uminho.sysbio.biosynthframework.ReferenceType;

@Entity
@Table(name="modelseed_metabolite_crossreference")
public class ModelSeedMetaboliteCrossreferenceEntity extends GenericCrossreference {

  private static final long serialVersionUID = 1L;

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="metabolite_id")
  private ModelSeedMetaboliteEntity modelSeedMetaboliteEntity;
  
  public ModelSeedMetaboliteCrossreferenceEntity() {
      super(null, null, null);
  }
  public ModelSeedMetaboliteCrossreferenceEntity(ReferenceType type, String reference, String value) {
      super(type, reference, value);
  }
  public ModelSeedMetaboliteCrossreferenceEntity(GenericCrossreference crossReference) {
      super(crossReference.getType(), crossReference.getRef(), crossReference.getValue());
  }
  
  public ModelSeedMetaboliteEntity getModelSeedMetaboliteEntity() { return modelSeedMetaboliteEntity;}
  public void setModelSeedMetaboliteEntity(ModelSeedMetaboliteEntity modelSeedMetaboliteEntity) {
    this.modelSeedMetaboliteEntity = modelSeedMetaboliteEntity;
  }
  
  
}
