package edu.uminho.biosynth.core.components.bigg;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.bigg.components.BiggReactionCrossReferenceEntity;

@Entity
@Table(name="BIGG_REACTION")
public class BiggReactionEntity extends GenericReaction{

	private static final long serialVersionUID = 1L;

	@Column(name="ENZYME") private String enzyme;
	public String getEnzyme() { return enzyme; }
	public void setEnzyme(String enzyme) { this.enzyme = enzyme; }

	@Column(name="EQUATION") private String equation;
	public String getEquation() { return equation; }
	public void setEquation(String equation) { this.equation = equation; }
	
	@Column(name="TRANSLOCATION") private boolean translocation;
	public boolean isTranslocation() { return translocation; }
	public void setTranslocation(boolean translocation) { this.translocation = translocation; }
	
	@ElementCollection
	@CollectionTable(name="BIGG_REACTION_COMPARTMENT", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="COMPARTMENT")
	private List<String> compartments = new ArrayList<> ();
	public List<String> getCompartments() { return compartments; }
	public void setCompartments(List<String> compartments) { this.compartments = compartments; }
	public void setCompartments(String[] compartments) { 
		this.compartments.clear();
		this.compartments.addAll(Arrays.asList(compartments)); }
	
	@ElementCollection
	@CollectionTable(name="BIGG_REACTION_SYNONYM", joinColumns=@JoinColumn(name="ID_REACTION"))
	@Column(name="SYNONYM")
	private List<String> synonyms = new ArrayList<> ();
	public List<String> getSynonyms() { return synonyms; }
	public void setSynonyms(List<String> synonyms) { this.compartments = synonyms; }
	public void setSynonyms(String[] synonyms) { 
		this.synonyms.clear();
		this.synonyms.addAll(Arrays.asList(synonyms)); }

	@OneToMany(mappedBy = "biggReactionEntity")
	private List<BiggReactionCrossReferenceEntity> crossReferences = new ArrayList<> ();
	public List<BiggReactionCrossReferenceEntity> getCrossReferences() { return crossReferences; }
	public void setCrossReferences(List<BiggReactionCrossReferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<>(crossReferences);
		for (BiggReactionCrossReferenceEntity crossReference : this.crossReferences) {
			crossReference.setBiggReactionEntity(this);
		}
	}
	public void addCrossReference(BiggReactionCrossReferenceEntity crossReference) {
		this.crossReferences.add(crossReference);
		crossReference.setBiggReactionEntity(this);
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append('\n');
		sb.append("EQUATION:").append(this.equation).append(sep);
		sb.append("TRANSLOCATION:").append(this.translocation).append(sep);
		sb.append("SYNONYMS:").append(this.synonyms).append(sep);
		sb.append("COMPARTMENTS:").append(this.compartments).append(sep);
		sb.append("CROSSREFERENCE:").append(this.crossReferences);
		return sb.toString();
	}
}
