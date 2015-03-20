package pt.uminho.sysbio.biosynth.chemanalysis.cdk;

import static org.junit.Assert.*;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.chemanalysis.cdk.CdkWrapper;

public class TestCdkWrapper {

	@Test
	public void testRemoveOnes() {
		String ret = CdkWrapper.toIsotopeMolecularFormula("C1H1O1", false);
		
		assertEquals("CHO", ret);
	}
	
	@Test
	public void testRemoveOnes_BiGG_Invalid_Example() {
		String ret = CdkWrapper.toIsotopeMolecularFormula("C20H29OFULLR2CO", false);

		assertEquals(null, ret);
	}
	
	@Test
	public void testAddOnes() {
		String ret = CdkWrapper.toIsotopeMolecularFormula("CHO", true);
		
		assertEquals("C1H1O1", ret);
	}
	
	@Test
	public void testMol2dToInchi() throws Exception {
		Pair<String, String> inchi = CdkWrapper.convertMol2dToInChI(" \n \n \n  3  2  0  0  0  0  0  0  0  0999 V2000\n   22.1250  -16.2017    0.0000 O   0  0  0  0  0  0  0  0  0  0  0  0\n   23.6000  -15.2112    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n   20.7129  -15.2859    0.0000 H   0  0  0  0  0  0  0  0  0  0  0  0\n  1  2  1  0     0  0\n  1  3  1  0     0  0\nM  END");
		assertEquals("InChI=1S/H2O/h1H2", inchi.getLeft());
	}
}
