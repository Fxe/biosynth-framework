package pt.uminho.sysbio.biosynthframework;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;
import pt.uminho.sysbio.biosynthframework.OptfluxContainerMetabolicModelEntity;

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
	private OptfluxContainerMetabolicModelEntity metabolicModel;
	public OptfluxContainerMetabolicModelEntity getMetabolicModel() { return metabolicModel;}
	public void setMetabolicModel(
			OptfluxContainerMetabolicModelEntity metabolicModel) {
		this.metabolicModel = metabolicModel;
	}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="metabolite_id")
	private DefaultModelMetaboliteEntity metabolite;
	public DefaultModelMetaboliteEntity getMetabolite() { return metabolite;}
	public void setMetabolite(DefaultModelMetaboliteEntity metabolite) { this.metabolite = metabolite;}
}
