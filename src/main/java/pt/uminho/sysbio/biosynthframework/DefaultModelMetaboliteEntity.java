package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import pt.uminho.sysbio.biosynthframework.DefaultMetabolicModelEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="optflux_container_metabolite")
public class DefaultModelMetaboliteEntity extends GenericMetabolite {

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
	
	@OneToMany(mappedBy = "metabolite", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<DefaultMetaboliteSpecie> species = new ArrayList<> ();
	public List<DefaultMetaboliteSpecie> getSpecies() { return species;}
	public void setSpecies(List<DefaultMetaboliteSpecie> species) {
		this.species = species;
	}
	
	private List<DefaultMetaboliteSpecieReference> crossreferences = new ArrayList<> ();
	public List<DefaultMetaboliteSpecieReference> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossreferences(
			List<DefaultMetaboliteSpecieReference> crossreferences) {
		this.crossreferences = crossreferences;
	}
}
