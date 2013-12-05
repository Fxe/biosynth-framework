package edu.uminho.biosynth.core.components;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public class GenericMetabolite extends AbstractGenericEntity implements Serializable {

	private static final long serialVersionUID = 134867731L;

	@Column(name="FORMULA") protected String formula;
	@Transient private Map<Integer, GenericReaction> rxnMap = new HashMap<> ();
	@Transient private Map<Integer, GenericEnzyme> ecnMap = new HashMap<> ();
	@Column(name="MCLASS") protected String metaboliteClass = "COMPOUND";
	
	public GenericMetabolite() {
		super(null);
		this.id = 0;
	}
	
	public GenericMetabolite(String id) {
		super(id);
		this.id = 0;
	}
	
	public GenericMetabolite(String id, int key) {
		super(id);
		this.id = key;
	}
	
	public void setFormula(String formula) {
		this.formula = formula;
	}
	public String getFormula() {
		return this.formula;
	}
	
	public Map<Integer, GenericReaction> getReactionMap() {
		return this.rxnMap;
	}
	public Map<Integer, GenericEnzyme> getEnzymeMap() {
		return this.ecnMap;
	}
	
	public Set<Integer> getReactionIdSet() {
		return this.rxnMap.keySet();
	}
	public void setReactionIdSet(Collection<Integer> reactionIdSet) {
		this.rxnMap.clear();
		for (Integer rxnId : reactionIdSet) {
			this.rxnMap.put(rxnId, null);
		}
	}
	public void addReactions(Collection<Integer> rxnIdCollection) {
		for (Integer rxnId : rxnIdCollection) {
			this.rxnMap.put(rxnId, null);
		}
	}
	
	public Set<Integer> getEnzymeIdSet() {
		return ecnMap.keySet();
	}
	public void setEnzymeIdSet(Collection<Integer> enzymeIdSet) {
		this.ecnMap.clear();
		for (Integer ecnId : enzymeIdSet) {
			this.ecnMap.put(ecnId, null);
		}
	}
	
	public String getMetaboliteClass() {
		return metaboliteClass;
	}
	public void setMetaboliteClass(String metaboliteClass) {
		this.metaboliteClass = metaboliteClass;
	}

	@Override
	public String toString() {
		final char sep = '\n';
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString()).append(sep);
		sb.append("Formula:").append(this.formula).append(sep);
		sb.append("Class:").append(this.metaboliteClass);
		return sb.toString();
	}
}
