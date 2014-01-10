package edu.uminho.biosynth.core.components.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggMetaboliteCrossReferenceEntity;

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
	
	@ElementCollection
	@CollectionTable(name="KEGG_METABOLITE_REACTION", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="REACTION")
	protected List<String> reactions = new ArrayList<> ();
	public List<String> getReactions() { return reactions;}
	public void setReactions(List<String> reactions) { this.reactions = reactions;}
	
	@ElementCollection
	@CollectionTable(name="KEGG_METABOLITE_PATHWAY", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="PATHWAY")
	protected List<String> pathways = new ArrayList<> ();
	
	@ElementCollection
	@CollectionTable(name="KEGG_METABOLITE_ENZYME", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="ENZYME")
	protected List<String> enzymes = new ArrayList<> ();
	
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
	
	public List<String> getEnzymes() {
		return enzymes;
	}
	public void setEnzymes(List<String> enzymes) {
		this.enzymes = enzymes;
	}
	

	
	public List<String> getPathways() {
		return pathways;
	}
	public void setPathways(List<String> pathways) {
		this.pathways = pathways;
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("entry:").append(entry).append(sep);
		sb.append("name:").append(name).append(sep);
		sb.append("formula:").append(formula).append(sep);
		sb.append("molw:").append(molWeight).append(sep);
		sb.append("mass:").append(mass).append(sep);
		sb.append("remark:").append(remark).append(sep);
		sb.append("comment:").append(comment).append(sep);
		sb.append("enzymes:").append(enzymes).append(sep);
		sb.append("pathways:").append(pathways).append(sep);
		sb.append("reactions:").append(reactions).append(sep);
		sb.append("xrefs:").append(crossReferences);
		return sb.toString();
	}
}
