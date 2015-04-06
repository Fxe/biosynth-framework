package pt.uminho.sysbio.biosynthframework.chemanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="chemical_structure_formula")
public class ChemicalFormula {
	
	@Id
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="formula", length=255, nullable=false, unique=true)
	private String formula;
	
	@Column(name="isotope_formula", length=255, nullable=true)
	private String isotopeFormula;
	
	@Column(name="generic", nullable=true)
	private Boolean generic;
	
	@Column(name="valid", nullable=true)
	private Boolean valid;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getIsotopeFormula() {
		return isotopeFormula;
	}

	public void setIsotopeFormula(String isotopeFormula) {
		this.isotopeFormula = isotopeFormula;
	}

	public Boolean getGeneric() {
		return generic;
	}

	public void setGeneric(Boolean generic) {
		this.generic = generic;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	
	@Override
	public String toString() {
		return String.format("ChemicalFormula[%s]<%s>", this.id, this.formula);
	}
}
