package pt.uminho.sysbio.biosynthframework.metabolicmodel.sbml;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="SBML_COMPARTMENT")
public class SbmlCompartment {
	
	@Id
	@Column(name="id", nullable=false, unique=true)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	
	@Column(name="entry", nullable=false)
	private String entry = "noIdAssigned";
	@Column(name="name")
	private String name;
	
	@ManyToOne
	@JoinColumn(name="ID_MODEL", nullable=false)
	private SbmlMetabolicModel sbmlMetabolicModel;
	public SbmlMetabolicModel getSbmlMetabolicModel() { return sbmlMetabolicModel;}
	public void setSbmlMetabolicModel(SbmlMetabolicModel sbmlMetabolicModel) { this.sbmlMetabolicModel = sbmlMetabolicModel;}
	
	public SbmlCompartment(String entry) { this.entry = entry;}
	public SbmlCompartment() { }
	
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry;}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
