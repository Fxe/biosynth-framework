package edu.uminho.biosynth.core.data.integration.chimera.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.MapKey;
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
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_name", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="name", length=2000, nullable=false)
	private Map<Long, List<String>> names = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_isotope_formula", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="iso_formula", length=255, nullable=false)
	private Map<Long, String> isoFormulas = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_formula", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="formula", length=255, nullable=false)
	private Map<Long, String> formulas = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_charge", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="charge", nullable=false)
	private Map<Long, Integer> charges = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_can_smiles", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="can_smiles", nullable=false)
	private Map<Long, String> canSmiles = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_smiles", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="smiles", nullable=false)
	private Map<Long, String> smiles = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_inchi", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="inchi", nullable=false)
	private Map<Long, String> inchis = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_models", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="model", nullable=false)
	private Map<Long, List<String>> models = new HashMap<> ();
	
//	@ElementCollection
//	@CollectionTable(name="integrated_metabolite_compartments", joinColumns=@JoinColumn(name="metabolite_id"))
//	@Column(name="compartment", nullable=false)
	private Map<Long, List<String>> compartments = new HashMap<> ();
	
	
	@OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER, mappedBy="integratedMetaboliteEntity")
	@MapKey(name="id")
	private Map<Long, IntegratedMetaboliteSourceProxy> sources = new HashMap<> ();
	public Map<Long, IntegratedMetaboliteSourceProxy> getSources() { return sources;}
	public void setSources(Map<Long, IntegratedMetaboliteSourceProxy> sources) {
		for (IntegratedMetaboliteSourceProxy proxy : sources.values()) {
			proxy.setIntegratedMetaboliteEntity(this);
		}
		this.sources = sources;
	}

	public List<IntegratedMetaboliteCrossreferenceEntity> getCrossreferences() {
		return crossreferences;
	}

	public void setCrossreferences(
			List<IntegratedMetaboliteCrossreferenceEntity> crossreferences) {
		this.crossreferences = crossreferences;
	}

	public Map<Long, List<String>> getNames() {
		return names;
	}
	public void setNames(Map<Long, List<String>> names) {
		this.names = names;
	}
	public Map<Long, String> getIsoFormulas() {
		return isoFormulas;
	}
	public void setIsoFormulas(Map<Long, String> isoFormulas) {
		this.isoFormulas = isoFormulas;
	}
	public Map<Long, String> getFormulas() {
		return formulas;
	}
	public void setFormulas(Map<Long, String> formulas) {
		this.formulas = formulas;
	}
	public Map<Long, Integer> getCharges() {
		return charges;
	}
	public void setCharges(Map<Long, Integer> charges) {
		this.charges = charges;
	}
	public Map<Long, String> getCanSmiles() {
		return canSmiles;
	}
	public void setCanSmiles(Map<Long, String> canSmiles) {
		this.canSmiles = canSmiles;
	}
	public Map<Long, String> getSmiles() {
		return smiles;
	}
	public void setSmiles(Map<Long, String> smiles) {
		this.smiles = smiles;
	}
	public Map<Long, String> getInchis() {
		return inchis;
	}
	public void setInchis(Map<Long, String> inchis) {
		this.inchis = inchis;
	}
	public Map<Long, List<String>> getModels() {
		return models;
	}
	public void setModels(Map<Long, List<String>> models) {
		this.models = models;
	}
	public Map<Long, List<String>> getCompartments() {
		return compartments;
	}
	public void setCompartments(Map<Long, List<String>> compartments) {
		this.compartments = compartments;
	}
	public Set<String> getProxyMajorDatabaseLabels() {
		Set<String> labels = new HashSet<> ();
		
		for (IntegratedMetaboliteSourceProxy proxy : this.sources.values()) {
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
 