package edu.uminho.biosynth.core.data.integration.domain.metabolite;

public class MolecularFormula {
	
	private String formula;
	
	private String isotopeFormula;

	/**
	 * Returns the molecular formula.
	 * @return the molecular formula string.
	 */
	public String getFormula() { return formula;}
	public void setFormula(String formula) { this.formula = formula;}

	/**
	 * Returns the major isotope molecular formula.
	 * @return the major isotope molecular formula string.
	 */
	public String getIsotopeFormula() { return isotopeFormula;}
	public void setIsotopeFormula(String isotopeFormula) { this.isotopeFormula = isotopeFormula;}
	
}
