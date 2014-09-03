package edu.uminho.biosynth.core.components.biodb.biocyc;

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

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.biodb.biocyc.components.BioCycMetaboliteCrossreferenceEntity;

@Entity
@Table(name="BIOCYC_METABOLITE")
public class BioCycMetaboliteEntity extends GenericMetabolite {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="MOLW") private Double molWeight;
	public Double getMolWeight() { return molWeight;}
	public void setMolWeight(Double molWeight) { this.molWeight = molWeight;}
	
	@Column(name="CMLMOLW") private Double cmlMolWeight;
	public Double getCmlMolWeight() { return cmlMolWeight;}
	public void setCmlMolWeight(Double cmlMolWeight) { this.cmlMolWeight = cmlMolWeight;}

	@Column(name="INCHI", length=16383) private String inChI;
	public String getInchi() { return inChI;}
	public void setInChI(String inChI) { this.inChI = inChI;}

	@Column(name="SMILES", length=16383) private String smiles;
	public String getSmiles() { return smiles;}
	public void setSmiles(String smiles) { this.smiles = smiles;}

	@Column(name="GIBBS") private Double gibbs;
	public Double getGibbs() { return gibbs;}
	public void setGibbs(Double gibbs) { this.gibbs = gibbs;}
	
	@Column(name="CHARGE") private Integer charge;
	public Integer getCharge() { return charge; }
	public void setCharge(Integer charge) { this.charge = charge; }
	
	@Column(name="B_COMMENT", length=16383) private String comment;
	public String getComment() { return comment;}
	public void setComment(String comment) { this.comment = comment;}
	
	@ElementCollection
	@CollectionTable(name="BIOCYC_METABOLITE_SUBCLASS", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="SUBCLASS", length=63)
	protected List<String> subclasses = new ArrayList<> ();
	public List<String> getSubclasses() { return subclasses;}
	public void setSubclasses(List<String> subclasses) { this.subclasses = subclasses;}

	@ElementCollection
	@CollectionTable(name="BIOCYC_METABOLITE_PARENT", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="PARENT", length=63)
	protected List<String> parents = new ArrayList<> ();
	public List<String> getParents() { return parents;}
	public void setParents(List<String> parents) { this.parents = parents;}

	@ElementCollection
	@CollectionTable(name="BIOCYC_METABOLITE_INSTANCE", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="INSTANCE", length=63)
	protected List<String> instances = new ArrayList<> ();
	public List<String> getInstances() { return instances;}
	public void setInstances(List<String> instances) { this.instances = instances;}

	@ElementCollection
	@CollectionTable(name="BIOCYC_METABOLITE_REACTION", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="REACTION", length=63)
	protected List<String> reactions = new ArrayList<> ();
	public List<String> getReactions() { return reactions;}
	public void setReactions(List<String> reactions) { this.reactions = reactions;}
	
	@ElementCollection
	@CollectionTable(name="BIOCYC_METABOLITE_SYNONYM", joinColumns=@JoinColumn(name="ID_METABOLITE"))
	@Column(name="SYNONYM", length=1023)
	private List<String> synonyms = new ArrayList<> ();
	public List<String> getSynonyms() { return synonyms;}
	public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms;}

	@OneToMany(mappedBy = "biocycMetaboliteEntity", cascade = CascadeType.ALL)
	private List<BioCycMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<>();
	public List<BioCycMetaboliteCrossreferenceEntity> getCrossreferences() { return crossReferences; }
	public void setCrossReferences(List<BioCycMetaboliteCrossreferenceEntity> crossReferences) {
		this.crossReferences = crossReferences;
		if (crossReferences != null)
		for (BioCycMetaboliteCrossreferenceEntity crossReference : crossReferences) 
			crossReference.setBiocycMetaboliteEntity(this);
	}
	public void addCrossReference(BioCycMetaboliteCrossreferenceEntity crossReference) {
		crossReference.setBiocycMetaboliteEntity(this);
		this.crossReferences.add(crossReference);
	}
	
	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("comment:").append(this.comment).append(sep);
		sb.append("molWeight:").append(this.molWeight).append(sep);
		sb.append("cmlMolWeight:").append(this.cmlMolWeight).append(sep);
		sb.append("charge:").append(this.charge).append(sep);
		sb.append("gibbs:").append(this.gibbs).append(sep);
		sb.append("Smiles:").append(this.smiles).append(sep);
		sb.append("InChI:").append(this.inChI).append(sep);
		sb.append("Reactions:").append(this.reactions).append(sep);
		sb.append("Synonyms:").append(this.synonyms).append(sep);
		sb.append("Parents:").append(this.parents).append(sep);
		sb.append("Subclasses:").append(this.subclasses).append(sep);
		sb.append("Instances:").append(this.instances).append(sep);
		sb.append("Crossreferences:").append(this.crossReferences);
		return sb.toString();
	}
}
