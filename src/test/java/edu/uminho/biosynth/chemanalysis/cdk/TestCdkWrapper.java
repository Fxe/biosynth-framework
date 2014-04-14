package edu.uminho.biosynth.chemanalysis.cdk;

import static org.junit.Assert.*;

import org.junit.Test;

public class TestCdkWrapper {

	@Test
	public void testRemoveOnes() {
		String ret = CdkWrapper.convertToIsotopeMolecularFormula("C1H1O1", false);
		
		assertEquals("CHO", ret);
	}
	
	@Test
	public void testAddOnes() {
		String ret = CdkWrapper.convertToIsotopeMolecularFormula("CHO", true);
		
		assertEquals("C1H1O1", ret);
	}

}
