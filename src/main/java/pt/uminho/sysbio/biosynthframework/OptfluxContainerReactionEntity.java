package pt.uminho.sysbio.biosynthframework;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.fasterxml.jackson.annotation.JsonIgnore;

import pt.uminho.sysbio.biosynthframework.annotations.MetaProperty;

@Entity
@Table(name="optflux_container_reaction")
public class OptfluxContainerReactionEntity extends GenericReaction {

	private static final long serialVersionUID = 1L;

	@MetaProperty
	@Column(name="gene_rule")
	private String geneRule;
	public String getGeneRule() { return geneRule;}
	public void setGeneRule(String geneRule) { this.geneRule = geneRule;}
	
	@ElementCollection
	@CollectionTable(name="optflux_container_reaction_gene", joinColumns=@JoinColumn(name="reaction_id"))
	@Column(name="gene", length=255)
	private Set<String> genes = new HashSet<> ();
	public Set<String> getGenes() { return genes;}
	public void setGenes(Set<String> genes) { this.genes = genes;}
	
	@JsonIgnore
	@MetaProperty
	@Column(name="lower_bound")
	private Double lowerBound;
	public double getLowerBound() { return lowerBound;}
	public void setLowerBound(Double lowerBound) { this.lowerBound = lowerBound;}
	
	@JsonIgnore
	@MetaProperty
	@Column(name="upper_bound")
	private Double upperBound;
	public double getUpperBound() { return upperBound;}
	public void setUpperBound(Double upperBound) { this.upperBound = upperBound;}
	
	@JsonIgnore
	@MetaProperty
	@Column(name="container_reversible")
	private boolean containerReversible;
	public boolean isContainerReversible() { return containerReversible;}
	public void setContainerReversible(boolean containerReversible) { this.containerReversible = containerReversible;}
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name="model_id")
	private DefaultMetabolicModelEntity metabolicModel;
	public DefaultMetabolicModelEntity getMetabolicModel() { return metabolicModel;}
	public void setMetabolicModel(
			DefaultMetabolicModelEntity metabolicModel) {
		this.metabolicModel = metabolicModel;
	}
	
	public void computeOrientation() {
		if (lowerBound != null && upperBound != null) {
			if (lowerBound < 0 && upperBound > 0) {
				this.orientation = Orientation.Reversible;
				return;
			}
			if (lowerBound >= 0) {
				this.orientation = Orientation.LeftToRight;
				return;
			}
			if (upperBound <= 0) {
				this.orientation = Orientation.RightToLeft;
				return;
			}
		} else {
			this.orientation = null;
		}
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<OptfluxContainerReactionLeft> left = new ArrayList<> ();
	public List<OptfluxContainerReactionLeft> getLeft() { return left;}
	public void setLeft(List<OptfluxContainerReactionLeft> left) {
		this.getReactantStoichiometry().clear();
		for (OptfluxContainerReactionLeft entity : left) {
			entity.setReaction(this);
			this.getReactantStoichiometry().put(entity.getCpdEntry(), entity.getStoichiometry());
		}
		this.left = left;
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "reaction", cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	private List<OptfluxContainerReactionRight> right = new ArrayList<> ();
	public List<OptfluxContainerReactionRight> getRight() { return right;}
	public void setRight(List<OptfluxContainerReactionRight> right) {
		this.getProductStoichiometry().clear();
		for (OptfluxContainerReactionRight entity : right) {
			entity.setReaction(this);
			this.getProductStoichiometry().put(entity.getCpdEntry(), entity.getStoichiometry());
		}
		this.right = right;
	}
	@Override
	public String toString() {
		String out = 
		entry + "\t" +
		left + "\t" +
//		right + "\t" + rightStoich + "\t" + rightCmp + "\t" + 
		translocation + "\t" + containerReversible + "\t" +
		lowerBound + "\t" + upperBound;
		return out;
	}
}
