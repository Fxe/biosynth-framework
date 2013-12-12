package edu.uminho.biosynth.core.components.representation;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import edu.uminho.biosynth.core.components.GenericMetabolite;
import edu.uminho.biosynth.core.components.GenericReaction;
import edu.uminho.biosynth.core.components.GenericReactionPair;

public class MetabolicStoichiometricMatrix implements IMetabolicRepresentation {

	private Map<String, GenericReaction> reactionDataMap;
	private Map<String, GenericMetabolite> metaboliteDataMap;
//	private Map<String, Integer> reactionIndexMap;
	private Map<String, Integer> metaboliteIndexMap;
	private boolean[] reversibility;
	private String[] metaNames;
	private String[] reacNames;
	private double[][] values;
	
	public MetabolicStoichiometricMatrix() {
		this.reactionDataMap = new HashMap<> ();
		this.metaboliteDataMap = new HashMap<> (); 
	}
	
	public void initializeMatrix() {
		this.metaboliteIndexMap = new HashMap<> ();
//		this.reactionIndexMap = new HashMap<> ();
		int numReactions = reactionDataMap.size();
		int numMetabolites = metaboliteDataMap.size();
		reversibility = new boolean[numReactions];
		metaNames = metaboliteDataMap.keySet().toArray(new String[0]);
		reacNames = reactionDataMap.keySet().toArray(new String[0]);
		
		values = new double[numMetabolites][];
		for (int i = 0; i < metaNames.length; i++) {
			values[i] = new double[numReactions];
			metaboliteIndexMap.put(metaNames[i], i);
		}
		for (int i = 0; i < reacNames.length; i++) {
//			reversibility[i] = reactionDataMap.get(reacNames[i]).isReversible();
//			reactionIndexMap.put(reacNames[i], i);
//			for (String cpdId : this.reactionDataMap.get(reacNames[i]).getReactantsID()) {
//				int cpdIndex = this.metaboliteIndexMap.get(cpdId);
//				this.values[cpdIndex][i] = 1;
//			}
//			for (String cpdId : this.reactionDataMap.get(reacNames[i]).getProductsID()) {
//				int cpdIndex = this.metaboliteIndexMap.get(cpdId);
//				this.values[cpdIndex][i] = -1;
//			}
		}
	}
	
	@Override
	public boolean isReactionPair() {
		return false;
	}

	@Override
	public boolean addReactionPair(GenericReactionPair rpr) {
		return false;
	}

	@Override
	public boolean addReactionPair(GenericReactionPair rpr,
			boolean duplicateForReverse) {
		return false;
	}

	@Override
	public boolean addReaction(GenericReaction rxn) {
		this.reactionDataMap.put(rxn.getEntry(), rxn);
//		for (GenericMetabolite cpd : rxn.getProducts()) this.addMetabolite(cpd);
//		for (GenericMetabolite cpd : rxn.getReactants()) this.addMetabolite(cpd);
		return true;
	}

	@Override
	public boolean addReaction(GenericReaction rxn, boolean duplicateForReverse) {
		return this.addReaction(rxn);
	}

	@Override
	public boolean addMetabolite(GenericMetabolite cpd) {
		this.metaboliteDataMap.put(cpd.getEntry(), cpd);
		return true;
	}

	@Override
	public boolean removeReaction() {
//		reactionDataMap.remove(key)
		return false;
	}

	@Override
	public boolean removeMetabolite() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean[] getReversibility() {
		return reversibility;
	}

	public void setReversibility(boolean[] reversibility) {
		this.reversibility = reversibility;
	}

	public String[] getMetaNames() {
		return metaNames;
	}

	public void setMetaNames(String[] metaNames) {
		this.metaNames = metaNames;
	}

	public String[] getReacNames() {
		return reacNames;
	}

	public void setReacNames(String[] reacNames) {
		this.reacNames = reacNames;
	}

	public double[][] getValues() {
		return values;
	}

	public void setValues(double[][] values) {
		this.values = values;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(Arrays.toString( this.reacNames)).append('\n');
		sb.append(Arrays.toString( this.metaNames)).append('\n');
		sb.append(Arrays.toString( this.reversibility)).append('\n');
		for (int i = 0; i < this.values.length; i++) {
			sb.append(Arrays.toString( this.values[i]))
			.append('\t').append(this.metaNames[i])
			.append('\t').append(this.metaboliteDataMap.get(metaNames[i]).getName()).append('\n');
		}
		return sb.toString();
	}
}
