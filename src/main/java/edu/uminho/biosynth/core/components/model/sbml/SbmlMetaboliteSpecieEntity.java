package edu.uminho.biosynth.core.components.model.sbml;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="sbml_specie")
public class SbmlMetaboliteSpecieEntity {

	@Id
	@Column(name="id", nullable=false, unique=true)
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long id;
	public Long getId() { return id;}
	public void setId(Long id) { this.id = id;}

	@Column(name="entry", nullable=false)
	private String entry = "noEntryAssigned";
	public String getEntry() { return entry;}
	public void setEntry(String entry) { this.entry = entry;}

	@Column(name="name", nullable=true, unique=false)
	private String name;
	public String getName() { return name;}
	public void setName(String name) { this.name = name;}
	
	@Column(name="charge", nullable=true, unique=false)
	private String charge;
	
	@Column(name="formula", nullable=true, unique=false)
	private String formula;

	private List<String> compartments = new ArrayList<> ();
	
	private List<String> reactions = new ArrayList<> ();
	
	public String getCharge() {
		return charge;
	}
	public void setCharge(String charge) {
		this.charge = charge;
	}
	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	

	public List<String> getReactions() {
		return reactions;
	}
	public void setReactions(List<String> reactions) {
		this.reactions = reactions;
	}
	public List<String> getCompartments() {
		return compartments;
	}
	public void setCompartments(List<String> compartments) {
		this.compartments = compartments;
	}

	@ManyToOne
	@JoinColumn(name="ID_COMPARTMENT", nullable=false)
	private SbmlCompartment sbmlCompartment;
	public SbmlCompartment getSbmlCompartment() { return sbmlCompartment;}
	public void setSbmlCompartment(SbmlCompartment sbmlCompartment) { this.sbmlCompartment = sbmlCompartment;}

	@ManyToOne
	@JoinColumn(name="ID_MODEL", nullable=false)
	private SbmlMetabolicModel sbmlMetabolicModel;
	public SbmlMetabolicModel getSbmlMetabolicModel() { return sbmlMetabolicModel;}
	public void setSbmlMetabolicModel(SbmlMetabolicModel sbmlMetabolicModel) { this.sbmlMetabolicModel = sbmlMetabolicModel;}
	
	public SbmlMetaboliteSpecieEntity() {}
	public SbmlMetaboliteSpecieEntity(String entry) { this.entry = entry;}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("name:").append(this.name).append(sep);
		sb.append("formula:").append(this.formula).append(sep);
		sb.append("charge:").append(this.charge).append(sep);
		sb.append("compartments:").append(this.compartments).append(sep);
		sb.append("reactions:").append(this.reactions);
		return sb.toString();
	}
}
