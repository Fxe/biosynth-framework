package edu.uminho.biosynth.core.components;

import java.io.Serializable;

public class AbstractGenericMetabolite extends AbstractGenericEntity implements IMetaboliteEntity, Serializable {

	private static final long serialVersionUID = 1L;
	protected String formula;

	public AbstractGenericMetabolite(String id) {
		super(id);
	}

	public String getFormula() {
		return formula;
	}
	public void setFormula(String formula) {
		this.formula = formula;
	}
	
	

}
