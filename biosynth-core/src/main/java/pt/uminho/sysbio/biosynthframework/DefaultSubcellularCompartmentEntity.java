package pt.uminho.sysbio.biosynthframework;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModelEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="optflux_container_compartment")
public class DefaultSubcellularCompartmentEntity extends AbstractBiosynthEntity {

	private static final long serialVersionUID = 1L;
	
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
