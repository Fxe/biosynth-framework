package pt.uminho.sysbio.biosynthframework;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.OptfluxContainerMetabolicModelEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="optflux_container_compartment")
public class DefaultSubcellularCompartmentEntity extends AbstractBiosynthEntity {

	private static final long serialVersionUID = 1L;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="model_id")
	private OptfluxContainerMetabolicModelEntity metabolicModel;
	public OptfluxContainerMetabolicModelEntity getMetabolicModel() { return metabolicModel;}
	public void setMetabolicModel(
			OptfluxContainerMetabolicModelEntity metabolicModel) {
		this.metabolicModel = metabolicModel;
	}
}
