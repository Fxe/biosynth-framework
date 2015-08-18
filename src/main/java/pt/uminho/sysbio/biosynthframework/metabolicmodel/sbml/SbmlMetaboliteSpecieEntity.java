package pt.uminho.sysbio.biosynthframework.metabolicmodel.sbml;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Table;

import pt.uminho.sysbio.biosynthframework.AbstractBiosynthEntity;
import pt.uminho.sysbio.biosynthframework.annotations.BiosynthEntity;

@BiosynthEntity(majorLabel="MetaboliteSpecie")
@Table(name="sbml_specie")
public class SbmlMetaboliteSpecieEntity extends AbstractBiosynthEntity{
	
	private static final long serialVersionUID = 1L;

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

//	@ManyToOne
//	@JoinColumn(name="ID_COMPARTMENT", nullable=false)
//	private SbmlCompartment sbmlCompartment;
//	public SbmlCompartment getSbmlCompartment() { return sbmlCompartment;}
//	public void setSbmlCompartment(SbmlCompartment sbmlCompartment) { this.sbmlCompartment = sbmlCompartment;}
//
//	@ManyToOne
//	@JoinColumn(name="ID_MODEL", nullable=false)
//	private SbmlMetabolicModel sbmlMetabolicModel;
//	public SbmlMetabolicModel getSbmlMetabolicModel() { return sbmlMetabolicModel;}
//	public void setSbmlMetabolicModel(SbmlMetabolicModel sbmlMetabolicModel) { this.sbmlMetabolicModel = sbmlMetabolicModel;}
	
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
