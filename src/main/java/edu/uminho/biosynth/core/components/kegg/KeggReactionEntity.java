package edu.uminho.biosynth.core.components.kegg;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericReaction;

@Entity
@Table(name="KEGG_REACTION")
public class KeggReactionEntity extends GenericReaction {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="DEFINITION") private String definition;
	public String getDefinition() { return definition; }
	public void setDefinition(String definition) { this.definition = definition; }
	
	@Column(name="EQUATION") private String equation;
	public String getEquation() { return equation; }
	public void setEquation(String equation) { this.equation = equation; }

	@Column(name="K_COMMENT") private String comment;
	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
	
	@Column(name="REMARK") private String remark;
	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }
	
	@ElementCollection
	@CollectionTable(name="KEGG_REACTION_ENZYME", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="ENZYME")
	protected List<String> enzymes = new ArrayList<> ();
	public List<String> getEnzymes() { return enzymes; }
	public void addEnzyme(String enzyme) { this.enzymes.add(enzyme); }
	public void setEnzymes(List<String> enzymes) { this.enzymes = enzymes; }

	@ElementCollection
	@CollectionTable(name="KEGG_REACTION_RPAIR", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="RPAIR")
	protected List<String> rpairs = new ArrayList<> ();
	public List<String> getRpairs() { return rpairs; }
	public void addRpair(String rpair) { this.rpairs.add(rpair); }
	public void setRpairs(List<String> rpairs) { this.rpairs = rpairs; }
	
	@ElementCollection
	@CollectionTable(name="KEGG_REACTION_ORTHOLOGY", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="ORTHOLOGY")
	protected List<String> orthologies = new ArrayList<> ();
	public List<String> getOrthologies() { return orthologies; }
	public void addOrthology(String orthology) { this.orthologies.add(orthology); }
	public void setOrthologies(List<String> orthologies) { this.orthologies = orthologies; }
	
	@ElementCollection
	@CollectionTable(name="KEGG_REACTION_PATHWAY", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="PATHWAY")
	protected List<String> pathways = new ArrayList<> ();
	public List<String> getPathways() { return pathways; }
	public void setPathways(List<String> pathways) { this.pathways = pathways; }
	
//	@OneToMany(mappedBy = "keggReactionEntity")
//	private List<KeggReactionCrossReferenceEntity> crossReferences = new ArrayList<> ();
//	public List<KeggReactionCrossReferenceEntity> getCrossReferences() { return crossReferences; }
//	public void addCrossReference(KeggReactionCrossReferenceEntity crossReference) {
//		crossReference.setKeggReactionEntity(this);
//		this.crossReferences.add(crossReference);
//	}
//	public void setCrossReferences(List<KeggReactionCrossReferenceEntity> crossReferences) {
//		this.crossReferences = new ArrayList<>(crossReferences);
//		for (KeggReactionCrossReferenceEntity crossReference : this.crossReferences) {
//			crossReference.setKeggReactionEntity(this);
//		}
//	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("Entry:").append(this.entry).append(sep);
		sb.append("Name:").append(this.name).append(sep);
		sb.append("Source:").append(this.source).append(sep);
		sb.append("Description:").append(this.description).append(sep);
		sb.append("Comment:").append(this.comment).append(sep);
		sb.append("Remark").append(this.remark).append(sep);
		sb.append("Definition").append(this.definition).append(sep);
		sb.append("Equation").append(this.equation).append(sep);
		sb.append("Rpairs").append(this.rpairs).append(sep);
		sb.append("Enzymes").append(this.enzymes).append(sep);
		sb.append("Pathways").append(this.pathways).append(sep);
		sb.append("Orthologies").append(this.orthologies);
		return sb.toString();
	}
}
