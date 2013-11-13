package edu.uminho.biosynth.core.components.kegg;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.kegg.components.KeggMetaboliteCrossReferenceEntity;

@Entity
@Table(name="KEGG_METABOLITE")
public class KeggMetaboliteEntity extends GenericMetabolite{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MASS") private double mass;
	@Column(name="MOLW") private double molWeight;
	
	@Column(name="K_COMMENT") private String comment;
	@Column(name="REMARK") private String remark;
	
	@OneToMany(mappedBy = "keggMetaboliteEntity")
	private List<KeggMetaboliteCrossReferenceEntity> crossReferences = new ArrayList<> ();
	
	public double getMass() {
		return mass;
	}
	public void setMass(double mass) {
		this.mass = mass;
	}
	
	public double getMolWeight() {
		return molWeight;
	}
	public void setMolWeight(double molWeight) {
		this.molWeight = molWeight;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	public List<KeggMetaboliteCrossReferenceEntity> getCrossReferences() {
		return crossReferences;
	}
	public void setCrossReferences(List<KeggMetaboliteCrossReferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<>(crossReferences);
		for (KeggMetaboliteCrossReferenceEntity crossReference : this.crossReferences) {
			crossReference.setKeggMetaboliteEntity(this);
		}
	}
	public void addCrossReference(KeggMetaboliteCrossReferenceEntity crossReference) {
		this.crossReferences.add(crossReference);
		crossReference.setKeggMetaboliteEntity(this);
	}
}
