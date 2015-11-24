package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="optflux_container_metabolite_specie")
public class DefaultMetaboliteSpecie extends GenericMetabolite {

  private static final long serialVersionUID = 1L;

  @MetaProperty
  @Column(name="compartment")
  private String comparment;
  public String getComparment() { return comparment;}
  public void setComparment(String comparment) { this.comparment = comparment;}

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="model_id")
  private DefaultMetabolicModelEntity metabolicModel;
  public DefaultMetabolicModelEntity getMetabolicModel() { return metabolicModel;}
  public void setMetabolicModel(
      DefaultMetabolicModelEntity metabolicModel) {
    this.metabolicModel = metabolicModel;
  }

  @MetaProperty
  @Column(name="entity_type")
  private EntityType entityType;
  public EntityType getEntityType() { return entityType;}
  public void setEntityType(EntityType entityType) { this.entityType = entityType;}

  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="metabolite_id")
  private DefaultModelMetaboliteEntity metabolite;
  public DefaultModelMetaboliteEntity getMetabolite() { return metabolite;}
  public void setMetabolite(DefaultModelMetaboliteEntity metabolite) { this.metabolite = metabolite;}

  private List<DefaultMetaboliteSpecieReference> crossreferences = new ArrayList<> ();
  public List<DefaultMetaboliteSpecieReference> getCrossreferences() {
    return crossreferences;
  }
  public void setCrossreferences(
      List<DefaultMetaboliteSpecieReference> crossreferences) {
    this.crossreferences = crossreferences;
  }


}
