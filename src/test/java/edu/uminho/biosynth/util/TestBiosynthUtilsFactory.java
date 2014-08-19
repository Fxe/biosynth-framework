package edu.uminho.biosynth.util;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.bigg.BiggMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.biocyc.BioCycMetaboliteEntity;
import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;

public class TestBiosynthUtilsFactory {
	
	@Test
	public void testKeggCpd1() {
		KeggCompoundMetaboliteEntity cpd = BioSynthUtilsFactory.buildKegg(
				"C00001", "water", "a molecule", "H2O", "", null, 18, 18, new String[]{"biocyc", "WATER"});
		assertEquals(1, cpd.getCrossReferences().size());
	}

	@Test
	public void testKeggCpd2() {
		KeggCompoundMetaboliteEntity cpd = BioSynthUtilsFactory.buildKegg(
				"C00001", "water", "a molecule", "H2O", "", null, 18, 18, new String[]{"biocyc"});
		assertEquals(0, cpd.getCrossReferences().size());
	}
	
	@Test
	public void testKeggCpd3() {
		KeggCompoundMetaboliteEntity cpd = BioSynthUtilsFactory.buildKegg(
				"C00001", "water", null, "H2O", "", "", 18, 18, 
				new String[] {"metacyc", "WATER", "bigg", "h2o"});
		assertEquals(2, cpd.getCrossReferences().size());
	}
	
	@Test
	public void testBiocycCpd1() {
		BioCycMetaboliteEntity cpd = BioSynthUtilsFactory.buildBiocyc(
				"WATER", "water", "a molecule", "H2O", null, 0, 18, 18, 20, new String[]{"kegg", "C00001", "same as", "http://rest.kegg.jp/get/C00001"});
		assertEquals(1, cpd.getCrossReferences().size());
	}

	@Test
	public void testBiocycCpd2() {
		BioCycMetaboliteEntity cpd = BioSynthUtilsFactory.buildBiocyc(
				"WATER", "water", "a molecule", "H2O", null, 0, 18, 18, 20, new String[]{"kegg", "C00001", "same as"});
		assertEquals(0, cpd.getCrossReferences().size());
	}
	
	@Test
	public void testBiggCpd1() {
		BiggMetaboliteEntity cpd = BioSynthUtilsFactory.buildBigg(
				"C00001", "water", "a molecule", "H2O", new String[]{"kegg", "C00001"});
		assertEquals(1, cpd.getCrossReferences().size());
	}

	@Test
	public void testBiggCpd2() {
		BiggMetaboliteEntity cpd = BioSynthUtilsFactory.buildBigg(
				"C00001", "water", "a molecule", "H2O", new String[]{"kegg"});
		assertEquals(0, cpd.getCrossReferences().size());
	}
}
