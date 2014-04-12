package edu.uminho.biosynth.chemanalysis.cdk;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openscience.cdk.exception.InvalidSmilesException;
import org.openscience.cdk.interfaces.IAtomContainer;
import org.openscience.cdk.interfaces.IChemObjectBuilder;
import org.openscience.cdk.interfaces.IMolecularFormula;
import org.openscience.cdk.silent.MolecularFormula;
import org.openscience.cdk.silent.SilentChemObjectBuilder;
import org.openscience.cdk.smiles.SmilesParser;
import org.openscience.cdk.tools.manipulator.MolecularFormulaManipulator;

public class TestCdkWrapper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		IChemObjectBuilder builder = SilentChemObjectBuilder.getInstance();
		
		
		IMolecularFormula f = MolecularFormulaManipulator.getMajorIsotopeMolecularFormula("H2O", builder);
		System.out.println(f);
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
