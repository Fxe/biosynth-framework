package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="optflux_container_model")
public class DefaultMetabolicModelEntity extends AbstractBiosynthEntity {

	private static final long serialVersionUID = 1L;

	@MetaProperty
	@Column(name="md5", length=32, nullable=false)
	private String md5;

	public String getMd5() { return md5;}
	public void setMd5(String md5) { this.md5 = md5;}
	
	@OneToMany(mappedBy = "metabolicModel", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<OptfluxContainerReactionEntity> reactions = new ArrayList<> ();
	public List<OptfluxContainerReactionEntity> getReactions() { return reactions;}
	public void setReactions(List<OptfluxContainerReactionEntity> reactions) { this.reactions = reactions;}
	
	@OneToMany(mappedBy = "metabolicModel", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<DefaultModelMetaboliteEntity> metabolites = new ArrayList<> ();
	public List<DefaultModelMetaboliteEntity> getMetabolites() { return metabolites;}
	public void setMetabolites(List<DefaultModelMetaboliteEntity> metabolites) { this.metabolites = metabolites;}
	
	@OneToMany(mappedBy = "metabolicModel", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<DefaultMetaboliteSpecie> species = new ArrayList<> ();
	public List<DefaultMetaboliteSpecie> getSpecies() { return species; }
	public void setSpecies(List<DefaultMetaboliteSpecie> species) { this.species = species;}

	@OneToMany(mappedBy = "metabolicModel", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	private List<DefaultSubcellularCompartmentEntity> subcellularCompartments = new ArrayList<> ();
	public List<DefaultSubcellularCompartmentEntity> getSubcellularCompartments() { return subcellularCompartments;}
	public void setSubcellularCompartments(
			List<DefaultSubcellularCompartmentEntity> subcellularCompartments) {
		this.subcellularCompartments = subcellularCompartments;
	}

	@Override
	public String toString() {
		return String.format("MetabolicModel[%d:%s]", id, entry);
	}
}
