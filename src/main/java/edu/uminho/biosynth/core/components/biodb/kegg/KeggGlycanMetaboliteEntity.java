package edu.uminho.biosynth.core.components.biodb.kegg;

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

import edu.uminho.biosynth.core.components.biodb.kegg.components.KeggGlycanMetaboliteCrossreferenceEntity;


@Entity
@Table(name="kegg_glycan_metabolite")
public class KeggGlycanMetaboliteEntity extends AbstractKeggMetabolite {
	
	private static final long serialVersionUID = 3012848505621097134L;

	@Column(name="mass")
	private String mass;
	public String getMass() { return mass;}
	public void setMass(String mass) { this.mass = mass;}

	@Column(name="glycan_class")
	private String compoundClass;
	public String getCompoundClass() { return compoundClass;}
	public void setCompoundClass(String compoundClass) { this.compoundClass = compoundClass;}
	
	@ElementCollection
	@CollectionTable(name="kegg_glycan_metabolite_reaction", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="reaction_id", length=15)
	private List<String> reactions = new ArrayList<> ();
	public List<String> getReactions() { return reactions;}
	public void setReactions(List<String> reactions) { this.reactions = reactions;}
	
	@ElementCollection
	@CollectionTable(name="kegg_glycan_metabolite_pathway", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="pathway_id", length=15)
	private List<String> pathways = new ArrayList<> ();
	public List<String> getPathways() { return pathways;}
	public void setPathways(List<String> pathways) { this.pathways = pathways;}
	
	@ElementCollection
	@CollectionTable(name="kegg_glycan_metabolite_enzyme", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="enzyme_id", length=15)
	private List<String> enzymes = new ArrayList<> ();
	public List<String> getEnzymes() { return enzymes;}
	public void setEnzymes(List<String> enzymes) { this.enzymes = enzymes;}
	
	@OneToMany(mappedBy = "keggGlycanMetaboliteEntity", cascade = CascadeType.ALL)
	private List<KeggGlycanMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
	
	public List<KeggGlycanMetaboliteCrossreferenceEntity> getCrossReferences() {
		return crossReferences;
	}
	public void setCrossReferences(List<KeggGlycanMetaboliteCrossreferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<>(crossReferences);
		for (KeggGlycanMetaboliteCrossreferenceEntity crossReference : this.crossReferences) {
			crossReference.setKeggGlycanMetaboliteEntity(this);
		}
	}
	public void addCrossReference(KeggGlycanMetaboliteCrossreferenceEntity crossReference) {
		this.crossReferences.add(crossReference);
		crossReference.setKeggGlycanMetaboliteEntity(this);
	}
}
