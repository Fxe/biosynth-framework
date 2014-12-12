package pt.uminho.sysbio.biosynthframework.util;

import java.util.Map;

public interface FormulaConverter {
	
	public Map<String, Integer> getAtomCountMap(String formula);
	public String convertToIsotopeMolecularFormula(String formula, boolean setOne);
}
