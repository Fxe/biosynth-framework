package edu.uminho.biosynth.core.components.biodb.kegg;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.biodb.kegg.components.KeegDrugMetaboliteCrossreferenceEntity;

@Entity
@Table(name="kegg_drug_metabolite")
public class KeggDrugMetaboliteEntity extends AbstractKeggMetabolite {

	private static final long serialVersionUID = -4475585298421446345L;
	
//	String component;
//	String source;
	
	@Column(name="mass") private Double mass;
	
	@Column(name="molw")
	private Double molWeight;
	
	@Column(name="target")
	private String target;
	
	@Column(name="activity")
	private String activity;
	
	@Column(name="metabolism")
	private String metabolism;
	
	@Column(name="product")
	private String product;
	
	@Column(name="sequence")
	private String sequence;
	
	@Column(name="str_map")
	private String strMap;
	
	@Column(name="other_map")
	private String otherMap;
	
	@Column(name="component")
	private String component;
	
	@Column(name="dr_source")
	private String drugSource;
	
	@Column(name="inchi") private String inchi;
	public String getInchi() { return inchi;}
	public void setInchi(String inchi) { this.inchi = inchi;}
	
	@Column(name="inchi_key") private String inchiKey;
	public String getInchiKey() { return inchiKey;}
	public void setInchiKey(String inchiKey) { this.inchiKey = inchiKey;}
	
	@Column(name="smiles") private String smiles;
	public String getSmiles() { return smiles;}
	public void setSmiles(String smiles) { this.smiles = smiles;}
	
//	String interaction;~

	public Double getMass() { return mass;}
	public void setMass(Double mass) { this.mass = mass;}
	
	public Double getMolWeight() { return molWeight;}
	public void setMolWeight(Double molWeight) { this.molWeight = molWeight;}
	
	public String getTarget() { return target;}
	public void setTarget(String target) { this.target = target;}
	
	public String getActivity() { return activity;}
	public void setActivity(String activity) { this.activity = activity;}
	
	public String getMetabolism() { return metabolism;}
	public void setMetabolism(String metabolism) { this.metabolism = metabolism;}
	
	public String getProduct() { return product;}
	public void setProduct(String product) { this.product = product;}
	
	public String getSequence() { return sequence;}
	public void setSequence(String sequence) {this.sequence = sequence;}
	
	public String getStrMap() { return strMap;}
	public void setStrMap(String strMap) { this.strMap = strMap;}
	
	public String getOtherMap() { return otherMap;}
	public void setOtherMap(String otherMap) { this.otherMap = otherMap;}
	
	public String getComponent() { return component;}
	public void setComponent(String component) { this.component = component; }
	

	public String getDrugSource() { return drugSource;}
	public void setDrugSource(String drugSource) { this.drugSource = drugSource;}

	@OneToMany(mappedBy = "keggDrugMetaboliteEntity", cascade = CascadeType.ALL)
	private List<KeegDrugMetaboliteCrossreferenceEntity> crossReferences = new ArrayList<> ();
	
	public List<KeegDrugMetaboliteCrossreferenceEntity> getCrossReferences() {
		return crossReferences;
	}
	public void setCrossReferences(List<KeegDrugMetaboliteCrossreferenceEntity> crossReferences) {
		this.crossReferences = new ArrayList<>(crossReferences);
		for (KeegDrugMetaboliteCrossreferenceEntity crossReference : this.crossReferences) {
			crossReference.setKeggDrugMetaboliteEntity(this);
		}
	}
	public void addCrossReference(KeegDrugMetaboliteCrossreferenceEntity crossReference) {
		this.crossReferences.add(crossReference);
		crossReference.setKeggDrugMetaboliteEntity(this);
	}

	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("mass:").append(mass).append(sep);
		sb.append("molWeight:").append(molWeight).append(sep);
		sb.append("target:").append(target).append(sep);
		sb.append("activity:").append(activity).append(sep);
		sb.append("metabolism:").append(metabolism).append(sep);
		sb.append("product:").append(product).append(sep);
		sb.append("component:").append(component).append(sep);
		sb.append("source:").append(drugSource).append(sep);
		sb.append("strMap:").append(strMap).append(sep);
		sb.append("otherMap:").append(otherMap).append(sep);
		sb.append("source:").append(drugSource).append(sep);
		sb.append("smiles:").append(smiles).append(sep);
		sb.append("inchi:").append(inchi).append(sep);
		sb.append("inchiKey:").append(inchiKey).append(sep);
		return sb.toString();
	}
}
