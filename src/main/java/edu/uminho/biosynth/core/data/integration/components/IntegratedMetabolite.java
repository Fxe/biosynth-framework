package edu.uminho.biosynth.core.data.integration.components;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Transient;

import edu.uminho.biosynth.core.components.GenericMetabolite;

public class IntegratedMetabolite extends GenericMetabolite {

	private static final long serialVersionUID = 1L;
	
	@Transient
	private List<GenericMetabolite> consensus = new ArrayList<> ();
	
	public void generateConsensus() {
		String name = "";
		String entry = "";
		String formula = "";
		for (GenericMetabolite cpd : consensus) {
			entry = entry.concat(cpd.getEntry());
			name = name.concat(cpd.getName());
			formula = formula.concat(cpd.getFormula());
		}
	}
}
