package pt.uminho.sysbio.biosynthframework.biodb.kegg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.GenericReaction;

@Entity
@Table(name="kegg_reaction")
public class KeggReactionEntity extends GenericReaction {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="definition", length=2047) private String definition;
	public String getDefinition() { return definition; }
	public void setDefinition(String definition) { this.definition = definition; }
	
	@Column(name="equation", length=2047) private String equation;
	public String getEquation() { return equation; }
	public void setEquation(String equation) { this.equation = equation; }

	@Column(name="k_comment", length=2047) private String comment;
	public String getComment() { return comment; }
	public void setComment(String comment) { this.comment = comment; }
	
	@Column(name="remark", length=1047) private String remark;
	public String getRemark() { return remark; }
	public void setRemark(String remark) { this.remark = remark; }
	
	@ElementCollection
	@CollectionTable(name="kegg_reaction_enzyme", joinColumns=@JoinColumn(name="reaction_id"))
	@Column(name="enzyme", length=31)
	protected List<String> enzymes = new ArrayList<> ();
	public List<String> getEnzymes() { return enzymes; }
	public void addEnzyme(String enzyme) { this.enzymes.add(enzyme); }
	public void setEnzymes(List<String> enzymes) { this.enzymes = enzymes; }

	@ElementCollection
	@CollectionTable(name="kegg_reaction_rpair", joinColumns=@JoinColumn(name="reaction_id"))
	@Column(name="rpair", length=15)
	protected List<String> rpairs = new ArrayList<> ();
	public List<String> getRpairs() { return rpairs; }
	public void addRpair(String rpair) { this.rpairs.add(rpair); }
	public void setRpairs(List<String> rpairs) { this.rpairs = rpairs; }
	
	@ElementCollection
	@CollectionTable(name="kegg_reaction_orthology", joinColumns=@JoinColumn(name="reaction_id"))
	@Column(name="orthology", length=15)
	protected List<String> orthologies = new ArrayList<> ();
	public List<String> getOrthologies() { return orthologies; }
	public void addOrthology(String orthology) { this.orthologies.add(orthology); }
	public void setOrthologies(List<String> orthologies) { this.orthologies = orthologies; }
	
	@ElementCollection
	@CollectionTable(name="kegg_reaction_pathway", joinColumns=@JoinColumn(name="reaction_id"))
	@Column(name="pathway", length=15)
	protected List<String> pathways = new ArrayList<> ();
	public List<String> getPathways() { return pathways; }
	public void setPathways(List<String> pathways) { this.pathways = pathways; }
	
	@OneToMany(mappedBy = "keggReactionEntity", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<KeggReactionLeftEntity> left = new ArrayList<> ();
	public List<KeggReactionLeftEntity> getLeft() { return left;}
	public void setLeft(List<KeggReactionLeftEntity> left) {
		this.getReactantStoichiometry().clear();
		for (KeggReactionLeftEntity entity : left) {
			entity.setKeggReactionEntity(this);
			this.getReactantStoichiometry().put(entity.getCpdEntry(), entity.getStoichiometry());
		}
		this.left = left;
	}
	
	@OneToMany(mappedBy = "keggReactionEntity", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<KeggReactionRightEntity> right = new ArrayList<> ();
	public List<KeggReactionRightEntity> getRight() { return right;}
	public void setRight(List<KeggReactionRightEntity> right) {
		this.getProductStoichiometry().clear();
		for (KeggReactionRightEntity entity : right) {
			entity.setKeggReactionEntity(this);
			this.getProductStoichiometry().put(entity.getCpdEntry(), entity.getStoichiometry());
		}
		this.right = right;
	}
	
	@JsonIgnore
	@Transient
	@Override
	public Map<String,Double> getLeftStoichiometry() {
		Map<String,Double> res = new HashMap<> ();
		for (KeggReactionLeftEntity l : left) {
			String entry = l.getCpdEntry();
			Double value = l.getStoichiometry();
			res.put(entry, value);
		}
		return res;
	};
	
	@JsonIgnore
	@Transient
	@Override
	public Map<String,Double> getRightStoichiometry() {
		Map<String,Double> res = new HashMap<> ();
		for (KeggReactionRightEntity r : right) {
			String entry = r.getCpdEntry();
			Double value = r.getStoichiometry();
			res.put(entry, value);
		}
		return res;
	};
	
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
		sb.append("Entry: ").append(this.entry).append(sep);
		sb.append("Name: ").append(this.name).append(sep);
		sb.append("Source: ").append(this.source).append(sep);
		sb.append("Description: ").append(this.description).append(sep);
		sb.append("Comment: ").append(this.comment).append(sep);
		sb.append("Remark: ").append(this.remark).append(sep);
		sb.append("Definition: ").append(this.definition).append(sep);
		sb.append("Equation: ").append(this.equation).append(sep);
		sb.append("Rpairs: ").append(this.rpairs).append(sep);
		sb.append("Enzymes: ").append(this.enzymes).append(sep);
		sb.append("Pathways: ").append(this.pathways).append(sep);
		sb.append("Orthologies: ").append(this.orthologies);
		return sb.toString();
	}
}
