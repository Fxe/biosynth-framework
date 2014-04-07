package edu.uminho.biosynth.chemanalysis.openbabel;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class TestOpenBabelProcessWrapper {

	@Test
	public void testInValidSmile1() throws IOException {
		String smile = "CH2(CH2(N(CH2(CH(C(CH2(O(C(OC(OHCH(OHCH3)CH(CH3CH3)))))CH(CH(O(C(OC(CH3CH(CH3))))){1}){8}))))<8>))<1>";
		String can = OpenBabelProcessWrapper.convertSmilesToCannonicalSmiles(smile);
		assertEquals(true, can == null);
	}
	
	@Test
	public void testErrorSmile1() throws IOException {
		String smile = "CCC(C)C(N=C([O-])CN=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(N=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(C)N=C([O-])C1CCCN1C(=O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(CC1=CC=CC=C1)N=C(O)C(CC1=CC=C(O)C=C1)N=C(O)C(CC1=CC=CC=C1)N=C(O)CN=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CC(O)=O)N=C(O)CN=C(O)C(CS)N=C(O)C(N=C(O)C(CC1=CC=CC=C1)N=C(O)C(CCC([O-])=[NH2+])N=C(O)C(CC(C)C)N=C(O)C(N=C(O)C(CC(O)=O)N=C(O)C(N=C(O)C(CC(C)C)N=C(O)C(CCC(O)=O)N=C(O)CN=C(O)CN=C(O)C(CS)N=C(O)C(CC(C)C)N=C([O-])C(N=C(O)C(CCC(O)=O)N=C(O)C(CO)N=C([O-])C1CCCN1C(=O)C(CCCNC(N)=[NH2+])N=C(O)C(CC1=CC=C(O)C=C1)N=C([O-])C(C)[NH3+])C(C)O)C(C)C)C(C)O)C(C)C)C(C)C)C(O)=NC(C(C)C)C(O)=NC(CCC(O)=O)C(O)=NC(CCC(O)=O)C(O)=NC(CS)C(O)=NC(CS)C(O)=NC(CC1=CC=CC=C1)C(O)=NC(CCCNC(N)=[NH2+])C(O)=NC(CO)C(O)=NC(CS)C(O)=NC(CC([O-])=O)C(O)=NC(CC(C)C)C(O)=NC(C)C([O-])=[NH+]C(CC(C)C)C(O)=NC(CC(C)C)C(O)=NC(CCC(O)=O)C(O)=NC(C(C)O)C(O)=NC(CC1=CC=C(O)C=C1)C(O)=NC(CS)C(O)=NC(C)C(O)=NC(C(C)O)C(=O)N1CCCC1C(O)=NC(C)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C([O-])=NC(CCC(O)=O)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C(O)=NC(CCC(O)=O)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C(O)=NC(CCC(O)=O)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C(O)=NC(CCC(O)=O)C(O)=O";
		String can = OpenBabelProcessWrapper.convertSmilesToCannonicalSmiles(smile);
		assertEquals(true, can == null);
	}
	
	@Test
	public void testValidSmile1() throws IOException {
		String smile = "CC(C)=CCC[C@](C)(OP([O-])(=O)OP([O-])([O-])=O)C=C";
		String can = OpenBabelProcessWrapper.convertSmilesToCannonicalSmiles(smile);
		assertEquals("C=C[C@@](OP(=O)(OP(=O)([O-])[O-])[O-])(CCC=C(C)C)C", can);
	}
	//C\C=C(\C)CC\C=C(\C)CC\C=C(\C)CC\C=C(\C)CC\C=C(\C)CC\C=C(\C)CC\C=C(\C)CC\C=C(\C)CC\C=C(/C)CC\C=C(/C)COP([O-])(=O)OP([O-])(=O)O[C@@H]1O[C@H](CO)[C@@H](O[C@@H]2O[C@@H](C)[C@H](O[C@@H]3OC([C@@H](CO)O[C@@H]4OC([C@H](O)CO[C@@H]5OC([C@@H](CO)O[C@@H]6OC([C@@H](CO[C@@H]7OC([C@@H](CO)O[C@@H]8OC([C@@H](CO[C@@H]9OC([C@@H](CO)O[C@@H]%10OC([C@@H](CO[C@@H]%11OC([C@@H](CO)O[C@@H]%12OC([C@H](O)CO[C@@H]%13OC([C@@H](CO)OC%14=C([O-])[C@@H](O)C(O%14)[C@H](O)CO[C@@H]%14OC([C@@H](CO)O[C@@H]%15OC([C@H](O)CO[C@@H]%16OC([C@@H](CO)O[C@@H]%17OC([C@H](O)CO[C@@H]%18OC([C@@H](CO)O[C@@H]%19OC([C@H](O)CO[C@@H]%20OC([C@@H](CO)O[C@@H]%21OC([C@H](O)CO[C@@H]%22OC([C@@H](CO)O[C@@H]%23OC([C@H](O)CO[C@@H]%24OC([C@@H](CO)O[C@@H]%25OC([C@H](O)CO[C@@H]%26OC([C@@H](CO)O[C@@H]%27OC([C@H](O)CO[C@@H]%28OC([C@@H](CO)O[C@@H]%29OC([C@H](O)CO[C@@H]%30OC([C@@H](CO)O[C@@H]%31OC([C@H](O)CO)[C@H](O)[C@H]%31O)[C@H](O)[C@H]%30O)[C@H](O)[C@H]%29O)[C@H](O)[C@H]%28O)[C@H](O)[C@H]%27O)[C@H](O)[C@H]%26O)[C@H](O)[C@H]%25O)[C@H](O)[C@H]%24O)[C@H](O)[C@H]%23O)[C@H](O)[C@H]%22O)[C@H](O)[C@H]%21O)[C@H](O)[C@H]%20O)[C@H](O)[C@H]%19O)[C@H](O)[C@H]%18O)[C@H](O)[C@H]%17O)[C@H](O)[C@H]%16O)[C@H](O)[C@H]%15O)[C@H](O)[C@H]%14O)[C@H](O)[C@H]%13O)[C@H](O)[C@H]%12O)[C@H](O)[C@H]%11O)OC%11O[C@H](COC%12O[C@H](COC%13O[C@H](COC%14O[C@H](COC%15O[C@H](COC%16O[C@H](COC%17O[C@H](COC%18O[C@H](COC%19O[C@H](COC%20O[C@H](CO)[C@@H](O)[C@@H]%20O)[C@@H](O)[C@@H]%19O)[C@@H](O)[C@@H]%18O)[C@@H](O)[C@@H]%17O)[C@@H](O)[C@@H]%16O)[C@@H](O[C@H]%16O[C@H](CO)[C@@H](O)[C@@H]%16O)[C@@H]%15O)[C@@H](O)[C@@H]%14O)[C@@H](O)[C@@H]%13O)[C@@H](O)[C@@H]%12O)[C@@H](O)[C@@H]%11O)[C@H](O)[C@H]%10O)[C@H](O)[C@H]9O)OC9O[C@H](COC%10O[C@H](COC%11O[C@H](COC%12O[C@H](COC%13O[C@H](COC%14O[C@H](COC%15O[C@H](COC%16O[C@H](COC%17O[C@H](COC%18O[C@H](CO)[C@@H](O)[C@@H]%18O)[C@@H](O)[C@@H]%17O)[C@@H](O)[C@@H]%16O)[C@@H](O)[C@@H]%15O)[C@@H](O)[C@@H]%14O)[C@@H](OC%14O[C@H](CO)[C@@H](O)[C@@H]%14O)[C@@H]%13O)[C@@H](O)[C@@H]%12O)[C@@H](O)[C@@H]%11O)[C@@H](O)[C@@H]%10O)[C@@H](O)[C@@H]9O)[C@H](O)[C@H]8O)[C@H](O)[C@H]7O)OC7O[C@H](COC8O[C@H](COC9O[C@H](COC%10O[C@H](COC%11O[C@H](COC%12O[C@H](COC%13O[C@H](COC%14O[C@H](COC%15O[C@H](COC%16O[C@H](CO)[C@@H](O)[C@@H]%16O)[C@@H](O)[C@@H]%15O)[C@@H](O)[C@@H]%14O)[C@@H](O)[C@@H]%13O)[C@@H](O)[C@@H]%12O)[C@@H](OC%12O[C@H](CO)[C@@H](O)[C@@H]%12O)[C@@H]%11O)[C@@H](O)[C@@H]%10O)[C@@H](O)[C@@H]9O)[C@@H](O)[C@@H]8O)[C@@H](O)[C@@H]7O)[C@H](O)[C@H]6O)[C@H](O)[C@H]5O)[C@H](O)[C@H]4O)[C@H](O)[C@H]3O)[C@@H](O)[C@H]2O)[C@H](O)[C@H]1NC(C)=O
	@Test
	public void testInValidSmile2() throws IOException {
		String smile = "CH(CH(C(OHC(OHO)))CH(C(OCH(CH2))))";
//		OpenBabelWrapper.initializeLibrary();
//		System.out.println(OpenBabelWrapper.convertSmilesToCannonicalSmiles(smile));
		String can = OpenBabelProcessWrapper.convertSmilesToCannonicalSmiles(smile);
		assertEquals(true, can == null);
	}
	
	@Test
	public void testValidInchi1() throws IOException {
		String inchi = "InChI=1S/C13H24O2/c1-3-4-5-6-7-8-9-10-11-12-15-13(2)14/h10-11H,3-9,12H2,1-2H3/b11-10+";
		String can = OpenBabelProcessWrapper.convertInchiToCannonicalSmiles(inchi);
		assertEquals("CCCCCCCC/C=C/COC(=O)C", can);
	}

}
