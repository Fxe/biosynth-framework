package edu.uminho.biosynth.core.data.integration.chimera.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteCrossreferenceEntity;
import edu.uminho.biosynth.core.data.integration.chimera.domain.components.IntegratedMetaboliteSourceProxy;

@Entity
@Table(name="integrated_metabolite")
public class IntegratedMetaboliteEntity extends GenericMetabolite {

	private static final long serialVersionUID = -6116044461015672826L;

	@OneToMany(mappedBy = "integratedMetaboliteEntity", cascade = CascadeType.ALL)
	private List<IntegratedMetaboliteCrossreferenceEntity> crossreferences = new ArrayList<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_name", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="name", length=2000, nullable=false)
	private Set<String> names = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_isotope_formula", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="iso_formula", length=255, nullable=false)
	private Set<String> isoFormulas = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_formula", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="formula", length=255, nullable=false)
	private Set<String> formulas = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_charge", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="charge", nullable=false)
	private Set<Integer> charges = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_can_smiles", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="can_smiles", nullable=false)
	private Set<String> canSmiles = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_smiles", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="smiles", nullable=false)
	private Set<String> smiles = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_inchi", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="inchi", nullable=false)
	private Set<String> inchis = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_models", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="model", nullable=false)
	private Set<String> models = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_compartments", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="compartment", nullable=false)
	private Set<String> compartments = new HashSet<> ();
	
	@ElementCollection
	@CollectionTable(name="integrated_metabolite_sources", joinColumns=@JoinColumn(name="metabolite_id"))
	@Column(name="source", nullable=false)
	private List<IntegratedMetaboliteSourceProxy> sources = new ArrayList<> ();

	public List<IntegratedMetaboliteCrossreferenceEntity> getCrossreferences() {
		return crossreferences;
	}

	public void setCrossreferences(
			List<IntegratedMetaboliteCrossreferenceEntity> crossreferences) {
		this.crossreferences = crossreferences;
	}

	public Set<String> getNames() { return names;}
	public void setNames(Set<String> names) { this.names = names;}

	public Set<String> getFormulas() { return formulas;}
	public void setFormulas(Set<String> formulas) { this.formulas = formulas;}

	public Set<Integer> getCharges() { return charges;}
	public void setCharges(Set<Integer> charges) { this.charges = charges;}

	public Set<String> getCanSmiles() { return canSmiles;}
	public void setCanSmiles(Set<String> canSmiles) { this.canSmiles = canSmiles;}

	public Set<String> getSmiles() { return smiles;}
	public void setSmiles(Set<String> smiles) { this.smiles = smiles;}

	public Set<String> getInchis() { return inchis;}
	public void setInchis(Set<String> inchis) { this.inchis = inchis;}
	
	public Set<String> getModels() { return models;}
	public void setModels(Set<String> models) { this.models = models;}

	public Set<String> getIsoFormulas() { return isoFormulas;}
	public void setIsoFormulas(Set<String> isoFormulas) { this.isoFormulas = isoFormulas;}

	public Set<String> getCompartments() { return compartments;}
	public void setCompartments(Set<String> compartments) { this.compartments = compartments;}
	
	

	public List<IntegratedMetaboliteSourceProxy> getSources() {
		return sources;
	}

	public void setSources(List<IntegratedMetaboliteSourceProxy> sources) {
		this.sources = sources;
	}
	
	
	public Set<String> getProxyMajorDatabaseLabels() {
		Set<String> labels = new HashSet<> ();
		
		for (IntegratedMetaboliteSourceProxy proxy : this.sources) {
			labels.add(proxy.getMajorLabel());
		}
		
		return labels;
	}

	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("names:").append(names).append(sep);
		sb.append("iso formulas:").append(isoFormulas).append(sep);
		sb.append("formulas:").append(formulas).append(sep);
		sb.append("charges:").append(charges).append(sep);
		sb.append("can:").append(canSmiles).append(sep);
		sb.append("smiles:").append(smiles).append(sep);
		sb.append("inchis:").append(inchis).append(sep);
		sb.append("compartments:").append(compartments).append(sep);
		sb.append("models:").append(models).append(sep);
		sb.append("sources:").append(sources).append(sep);
		sb.append("xrefs:").append(crossreferences);
		return sb.toString();
	}
}
 