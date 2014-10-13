package pt.uminho.sysbio.biosynthframework.biodb.kegg;

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
import javax.xml.bind.annotation.XmlRootElement;

import pt.uminho.sysbio.biosynthframework.annotations.InChI;
import pt.uminho.sysbio.biosynthframework.annotations.SMILES;

@Entity
@Table(name="kegg_compound_metabolite")
@XmlRootElement(name="KeggCompound")
public class KeggCompoundMetaboliteEntity extends AbstractKeggMetabolite{
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="mass") private Double mass;
	public Double getMass() { return mass;}
	public void setMass(Double mass) { this.mass = mass;}
	
	@Column(name="molw") private Double molWeight;
	public Double getMolWeight() { return molWeight;}
	public void setMolWeight(Double molWeight) { this.molWeight = molWeight;}
	
	@InChI
	@Column(name="inchi") private String inchi;
	public String getInchi() { return inchi;}
	public void setInchi(String inchi) { this.inchi = inchi;}
	
	@Column(name="inchi_key") private String inchiKey;
	public String getInchiKey() { return inchiKey;}
	public void setInchiKey(String inchiKey) { this.inchiKey = inchiKey;}
	
	@SMILES
	@Column(name="smiles") private String smiles;
	public String getSmiles() { return smiles;}
	public void setSmiles(String smiles) { this.smiles = smiles;}
	
	@OneToMany(mappedBy = "keggMetaboliteEntity", cascade = CascadeType.ALL)
	private List<KeggCompoundMetaboliteCrossreferenceEntity> crossreferences = new ArrayList<> ();
	
	@ElementCollection
	@CollectionTable(name="kegg_compound_metabolite_reaction", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="reaction_id", length=15)
	protected List<String> reactions = new ArrayList<> ();
	public List<String> getReactions() { return reactions;}
	public void setReactions(List<String> reactions) { this.reactions = reactions;}
	
	@ElementCollection
	@CollectionTable(name="kegg_compound_metabolite_pathway", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="pathway_id", length=15)
	protected List<String> pathways = new ArrayList<> ();
	
	@ElementCollection
	@CollectionTable(name="kegg_compound_metabolite_enzyme", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="enzyme_id", length=15)
	protected List<String> enzymes = new ArrayList<> ();

	public List<KeggCompoundMetaboliteCrossreferenceEntity> getCrossreferences() {
		return crossreferences;
	}
	public void setCrossReferences(List<KeggCompoundMetaboliteCrossreferenceEntity> crossreferences) {
		this.crossreferences = new ArrayList<>(crossreferences);
		for (KeggCompoundMetaboliteCrossreferenceEntity crossreference : this.crossreferences) {
			crossreference.setKeggMetaboliteEntity(this);
		}
	}
	public void addCrossReference(KeggCompoundMetaboliteCrossreferenceEntity crossreference) {
		this.crossreferences.add(crossreference);
		crossreference.setKeggMetaboliteEntity(this);
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
		sb.append(super.toString()).append(sep);
		sb.append("entry:").append(entry).append(sep);
		sb.append("name:").append(name).append(sep);
		sb.append("formula:").append(formula).append(sep);
		sb.append("molw:").append(molWeight).append(sep);
		sb.append("mass:").append(mass).append(sep);
		sb.append("enzymes:").append(enzymes.size() > 10 ? String.format("%d Enzymes", enzymes.size()) : enzymes).append(sep);
		sb.append("pathways:").append(pathways).append(sep);
		sb.append("reactions:").append(reactions).append(sep);
		sb.append("xrefs:").append(crossreferences);
		return sb.toString();
	}
}
