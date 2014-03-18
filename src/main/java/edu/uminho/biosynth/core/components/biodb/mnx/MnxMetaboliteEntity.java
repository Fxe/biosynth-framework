package edu.uminho.biosynth.core.components.biodb.mnx;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.biodb.mnx.components.MnxMetaboliteCrossReferenceEntity;

@Entity
@Table(name="MNX_METABOLITE")
public class MnxMetaboliteEntity extends GenericMetabolite{

	private static final long serialVersionUID = 1L;
	
	@Column(name="CHARGE") private Integer charge;
    @Column(name="O_SOURCE", length=255) private String originalSource;
    @Column(name="SMILES", length=16383) private String smiles;
    @Column(name="INCHI", length=16383) private String inChI;
    @Column(name="MASS") private Double mass;
    
    @OneToMany(mappedBy = "mnxMetaboliteEntity", cascade = CascadeType.ALL)
    private List<MnxMetaboliteCrossReferenceEntity> crossReferences = new ArrayList<>();

	public Integer getCharge() {
		return charge;
	}
	public void setCharge(Integer charge) {
		this.charge = charge;
	}

	public String getOriginalSource() {
		return originalSource;
	}
	public void setOriginalSource(String originalSource) {
		this.originalSource = originalSource;
	}

	public String getSmiles() {
		return smiles;
	}
	public void setSmiles(String smiles) {
		this.smiles = smiles;
	}

	public String getInChI() {
		return inChI;
	}
	public void setInChI(String inChI) {
		this.inChI = inChI;
	}

	public Double getMass() {
		return mass;
	}
	public void setMass(Double mass) {
		this.mass = mass;
	}
	
	public List<MnxMetaboliteCrossReferenceEntity> getCrossReferences() {
		return crossReferences;
	}
	public void addCrossReference(MnxMetaboliteCrossReferenceEntity crossReference) {
		crossReference.setMnxMetaboliteEntity(this);
		this.crossReferences.add(crossReference);
	}
	public void setCrossReferences(
			List<MnxMetaboliteCrossReferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<> (crossReferences);
		for (MnxMetaboliteCrossReferenceEntity crossReference : crossReferences) {
			crossReference.setMnxMetaboliteEntity(this);
		}
	}
    
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("Charge:").append(charge).append(sep);
		sb.append("Original Source:").append(originalSource).append(sep);
		sb.append("Smiles:").append(smiles).append(sep);
		sb.append("InChI:").append(inChI).append(sep);
		sb.append("mass:").append(mass).append(sep);
		sb.append("xrefs:").append(crossReferences);
		return sb.toString();
	}
}
