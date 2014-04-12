package edu.uminho.biosynth.chemanalysis.cdk;

import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.silent.MolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;

public class CdkWrapper {

	public void test() {
		IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
		MolecularFormula formula1 = new MolecularFormula();
		SmilesParser parser = new SmilesParser(builder);
		try {
			IAtomContainer atomContainer = parser.parseSmiles("[H]O[H]");
			System.out.println(atomContainer);
		} catch (InvalidSmilesException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
