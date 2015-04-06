package pt.uminho.sysbio.biosynth.chemanalysis.opsin;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.chemanalysis.inchi.JniInchi;
import pt.uminho.sysbio.biosynthframework.chemanalysis.opsin.OpsinWrapper;

public class TestOpsinWrapper {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		String omg = JniInchi.getInchiKeyFromInchi("InChI=1S/C8H13NO/c1-9-6-2-3-7(9)5-8(10)4-6/h6-7H,2-5H2,1H3");
		System.out.println(omg);
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
	public void testConvertToSmilesWithValidIupacName1() throws Exception {
		String smiles = OpsinWrapper.iupacToSmiles(
				"N-(3-[[(carboxyacetyl)[4-[(Z)-(2,4-dioxo-1,3-thiazolidin-5-ylidene)methyl]phenyl]amino]methyl]benzoyl)-D-glutamic acid");
		
		
		assertEquals("C(=O)(O)CC(=O)N(C1=CC=C(C=C1)\\C=C/1\\C(NC(S1)=O)=O)CC=1C=C(C(=O)N[C@H](CCC(=O)O)C(=O)O)C=CC1", smiles);
	}

	@Test
	public void testConvertToInchiWithValidIupacName1() throws Exception {
		String inchi = OpsinWrapper.iupacToInchi(
				"N-(3-[[(carboxyacetyl)[4-[(Z)-(2,4-dioxo-1,3-thiazolidin-5-ylidene)methyl]phenyl]amino]methyl]benzoyl)-D-glutamic acid");
		
		
		assertEquals("InChI=1S/C26H23N3O10S/c30-20(12-22(33)34)29(17-6-4-14(5-7-17)11-19-24(36)28-26(39)40-19)13-15-2-1-3-16(10-15)23(35)27-18(25(37)38)8-9-21(31)32/h1-7,10-11,18H,8-9,12-13H2,(H,27,35)(H,31,32)(H,33,34)(H,37,38)(H,28,36,39)/b19-11-/t18-/m1/s1", inchi);
	}
	
	@Test
	public void testConvertToInchiWithInvalidIupacName1() throws Exception {
		String inchi = OpsinWrapper.iupacToInchi("Foo ?");
		
		assertEquals(null, inchi);
	}
}
