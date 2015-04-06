package pt.uminho.sysbio.biosynthframework.chemanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="chemical_structure_core")
public class ChemicalStructureCore {
	
	@Id
	@GeneratedValue
	@Column(name="id", nullable=false)
	private Integer id;
	
	@Column(name="inchi_node", nullable=true)
	private Long inchiNodeId;
	
	@Column(name="can_node", nullable=true)
	private Long canNodeId;
	
	@Column(name="inchi", length=20000, nullable=true)
	private String inchi;
	
	@Column(name="inchi_key", length=255, nullable=true)
	private String inchiKey;

	@Column(name="can", length=2047, nullable=true)
	private String can;
	
	@Column(name="charge", nullable=true)
	private Integer charge;

	@Column(name="formula", length=255, nullable=true)
	private String formula;
	
	@Column(name="valid", nullable=true)
	private Boolean valid;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getInchi() {
		return inchi;
	}

	public void setInchi(String inchi) {
		this.inchi = inchi;
	}

	public String getInchiKey() {
		return inchiKey;
	}

	public void setInchiKey(String inchiKey) {
		this.inchiKey = inchiKey;
	}

	public String getCan() {
		return can;
	}

	public void setCan(String can) {
		this.can = can;
	}

	public Integer getCharge() {
		return charge;
	}

	public void setCharge(Integer charge) {
		this.charge = charge;
	}

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Long getInchiNodeId() {
		return inchiNodeId;
	}

	public void setInchiNodeId(Long inchiNodeId) {
		this.inchiNodeId = inchiNodeId;
	}

	public Long getCanNodeId() {
		return canNodeId;
	}

	public void setCanNodeId(Long canNodeId) {
		this.canNodeId = canNodeId;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Formula:").append(formula).append('\n');
		sb.append("Charge:").append(charge).append('\n');
		sb.append("Inchi:").append(inchi).append('\n');
		sb.append("InchiKey:").append(inchiKey).append('\n');
		sb.append("Can:").append(can);
		return sb.toString();
	}
}
