package edu.uminho.biosynth.core.components.mnx;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionCrossReferenceEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionProductEntity;
import edu.uminho.biosynth.core.components.mnx.components.MnxReactionReactantEntity;

@Entity
@Table(name="MNX_REACTION")
public class MnxReactionEntity extends GenericReaction {

	private static final long serialVersionUID = 1L;
	
	@Column(name="BALANCED") private boolean balanced;
	@Column(name="DEFINITION") private String definition;
	@Column(name="EQUATION") private String equation;
	@Column(name="O_SOURCE") private String originalSource;
	@Column(name="VARIABLE") private boolean variable;
	
	@OneToMany(mappedBy = "mnxReactionEntity", cascade = CascadeType.ALL)
	private List<MnxReactionReactantEntity> left = new ArrayList<> ();
	
	@OneToMany(mappedBy = "mnxReactionEntity", cascade = CascadeType.ALL)
	private List<MnxReactionProductEntity> right = new ArrayList<> ();
	
    @OneToMany(mappedBy = "mnxReactionEntity", cascade = CascadeType.ALL)
    private List<MnxReactionCrossReferenceEntity> crossReferences = new ArrayList<>();
    
	@ElementCollection
	@CollectionTable(name="MNX_REACTION_ENZYME", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="ENZYME")
	protected List<String> enzymes = new ArrayList<> ();
	public List<String> getEnzymes() { return enzymes; }
	public void addEnzyme(String enzyme) { this.enzymes.add(enzyme); }
	public void setEnzymes(List<String> enzymes) { this.enzymes = enzymes; }
	
	public boolean isBalanced() {
		return balanced;
	}
	public void setBalanced(boolean balanced) {
		this.balanced = balanced;
	}
	
	public String getDefinition() {
		return definition;
	}
	public void setDefinition(String definition) {
		this.definition = definition;
	}
	
	public String getEquation() {
		return equation;
	}
	public void setEquation(String equation) {
		this.equation = equation;
	}
	
	public String getOriginalSource() {
		return originalSource;
	}
	public void setOriginalSource(String originalSource) {
		this.originalSource = originalSource;
	}
	
	public boolean isVariable() {
		return variable;
	}
	public void setVariable(boolean variable) {
		this.variable = variable;
	}
	
	public List<MnxReactionReactantEntity> getLeft() {
		return left;
	}
	public void addLeft(MnxReactionReactantEntity stoichiometryPair) {
		stoichiometryPair.setMnxReactionEntity(this);
		this.left.add(stoichiometryPair);
	}
	public void setLeft(List<MnxReactionReactantEntity> left) {
		this.left = left;
	}
	
	public List<MnxReactionProductEntity> getRight() {
		return right;
	}
	public void addRight(MnxReactionProductEntity stoichiometryPair) {
		stoichiometryPair.setMnxReactionEntity(this);
		this.right.add(stoichiometryPair);
	}
	public void setRight(List<MnxReactionProductEntity> right) {
		this.right = right;
	}
	
	public List<MnxReactionCrossReferenceEntity> getCrossReferences() {
		return crossReferences;
	}
	public void addCrossReference(MnxReactionCrossReferenceEntity crossReference) {
		crossReference.setMnxReactionEntity(this);
		this.crossReferences.add(crossReference);
	}
	public void setCrossReferences(
			List<MnxReactionCrossReferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<> (crossReferences);
		for (MnxReactionCrossReferenceEntity crossReference : crossReferences) {
			crossReference.setMnxReactionEntity(this);
		}
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append("ID:").append(this.id).append(sep);
		sb.append("ENTRY:").append(this.getEntry()).append(sep);
		sb.append("SOURCE:").append(this.source).append(sep);
		sb.append("NAME:").append(name).append(sep);
		sb.append("DEFINITION:").append(this.definition).append(sep);
		sb.append("EQUATION:").append(this.equation).append(sep);
		sb.append("BALANCED:").append(this.balanced).append(sep);
		sb.append("VARIABLE:").append(this.variable).append(sep);
		sb.append("LEFT:").append(this.left).append(sep);
		sb.append("RIGHT:").append(this.right).append(sep);
		sb.append("O_SOURCE:").append(this.originalSource).append(sep);
		sb.append("REV:").append(this.orientation).append(sep);
		sb.append("ENZYMES:").append(this.getEnzymes()).append(sep);
		sb.append("XREF:").append(this.crossReferences).append(sep);
		return sb.toString();
	}
}
