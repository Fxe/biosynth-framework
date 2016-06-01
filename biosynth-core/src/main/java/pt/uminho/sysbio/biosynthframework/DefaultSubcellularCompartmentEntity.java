package pt.uminho.sysbio.biosynthframework;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModelEntity;
import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="optflux_container_compartment")
public class DefaultSubcellularCompartmentEntity extends AbstractBiosynthEntity {

  private static final long serialVersionUID = 1L;

  @MetaProperty
  @Column(name="compartment")
  private SubcellularCompartment compartment = SubcellularCompartment.UNKNOWN;
  public SubcellularCompartment getCompartment() { return compartment;}
  public void setCompartment(SubcellularCompartment compartment) { this.compartment = compartment;}
  
  @JsonIgnore
  @ManyToOne
  @JoinColumn(name="model_id")
  private DefaultMetabolicModelEntity metabolicModel;
  public DefaultMetabolicModelEntity getMetabolicModel() { return metabolicModel;}
  public void setMetabolicModel(
      DefaultMetabolicModelEntity metabolicModel) {
    this.metabolicModel = metabolicModel;
  }
}
