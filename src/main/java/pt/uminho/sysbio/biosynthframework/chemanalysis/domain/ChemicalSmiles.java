package pt.uminho.sysbio.biosynthframework.chemanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="chemical_structure_smiles")
public class ChemicalSmiles {

	@Id
//	@GeneratedValue
	@Column(name="id", nullable=false)
	private Long id;
	
	@Column(name="smiles", length=2047, nullable=true)
	private String smiles;
	
	@Column(name="valid", nullable=true)
	private Boolean valid;
	
	@Column(name="can_node", nullable=true)
	private Long canNodeId;
	
	@Column(name="can", length=2047, nullable=true)
	private String can;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getSmiles() {
		return smiles;
	}

	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	public Long getCanNodeId() {
		return canNodeId;
	}

	public void setCanNodeId(Long canNodeId) {
		this.canNodeId = canNodeId;
	}

	public String getCan() {
		return can;
	}

	public void setCan(String can) {
		this.can = can;
	}


	
	
}
