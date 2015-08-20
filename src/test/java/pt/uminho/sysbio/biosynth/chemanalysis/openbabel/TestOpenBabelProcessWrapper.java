package pt.uminho.sysbio.biosynth.chemanalysis.openbabel;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.chemanalysis.openbabel.OpenBabelProcessWrapper;

public class TestOpenBabelProcessWrapper {
	
	@Test
	public void testInvalidSmile1() throws IOException {
		String smile = "CH2(CH2(N(CH2(CH(C(CH2(O(C(OC(OHCH(OHCH3)CH(CH3CH3)))))CH(CH(O(C(OC(CH3CH(CH3))))){1}){8}))))<8>))<1>";
		String can = OpenBabelProcessWrapper.convert("smi", "can", smile);
		assertEquals(true, can == null);
	}
	
	@Test
	public void testInvalidSmile2() throws IOException {
		String smile = "CH(CH(C(OHC(OHO)))CH(C(OCH(CH2))))";
		String can = OpenBabelProcessWrapper.convert("smi", "can", smile);
		assertEquals(true, can == null);
	}
	
	@Test
	public void testValidSmile1() throws IOException {
		String smile = "CC(C)=CCC[C@](C)(OP([O-])(=O)OP([O-])([O-])=O)C=C";
		String can = OpenBabelProcessWrapper.convert("smi", "can", smile);
		assertEquals("C=C[C@@](OP(=O)(OP(=O)([O-])[O-])[O-])(CCC=C(C)C)C", can);
	}
	
	@Test
	public void testValidSmile2() throws IOException {
		String smile = "CCC(C)C(N=C([O-])CN=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(N=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(C)N=C([O-])C1CCCN1C(=O)C(CCCNC(N)=[NH2+])N=C(O)C(CO)N=C(O)C(CC1=CC=CC=C1)N=C(O)C(CC1=CC=C(O)C=C1)N=C(O)C(CC1=CC=CC=C1)N=C(O)CN=C(O)C(CCCNC(N)=[NH2+])N=C(O)C(CC(O)=O)N=C(O)CN=C(O)C(CS)N=C(O)C(N=C(O)C(CC1=CC=CC=C1)N=C(O)C(CCC([O-])=[NH2+])N=C(O)C(CC(C)C)N=C(O)C(N=C(O)C(CC(O)=O)N=C(O)C(N=C(O)C(CC(C)C)N=C(O)C(CCC(O)=O)N=C(O)CN=C(O)CN=C(O)C(CS)N=C(O)C(CC(C)C)N=C([O-])C(N=C(O)C(CCC(O)=O)N=C(O)C(CO)N=C([O-])C1CCCN1C(=O)C(CCCNC(N)=[NH2+])N=C(O)C(CC1=CC=C(O)C=C1)N=C([O-])C(C)[NH3+])C(C)O)C(C)C)C(C)O)C(C)C)C(C)C)C(O)=NC(C(C)C)C(O)=NC(CCC(O)=O)C(O)=NC(CCC(O)=O)C(O)=NC(CS)C(O)=NC(CS)C(O)=NC(CC1=CC=CC=C1)C(O)=NC(CCCNC(N)=[NH2+])C(O)=NC(CO)C(O)=NC(CS)C(O)=NC(CC([O-])=O)C(O)=NC(CC(C)C)C(O)=NC(C)C([O-])=[NH+]C(CC(C)C)C(O)=NC(CC(C)C)C(O)=NC(CCC(O)=O)C(O)=NC(C(C)O)C(O)=NC(CC1=CC=C(O)C=C1)C(O)=NC(CS)C(O)=NC(C)C(O)=NC(C(C)O)C(=O)N1CCCC1C(O)=NC(C)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C([O-])=NC(CCC(O)=O)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C(O)=NC(CCC(O)=O)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C(O)=NC(CCC(O)=O)C(O)=NC(CCCC[NH3+])C(O)=NC(CO)C(O)=NC(CCC(O)=O)C(O)=O";
		String can = OpenBabelProcessWrapper.convert("smi", "can", smile);
		assertEquals("[NH3+]CCCCC(C(=NC(C(=NC(C(=NC(C(=NC(C(=NC(C(=NC(C(=NC(C(=NC(C(=NC(C(=NC(C(=NC(C(=O)O)CCC(=O)O)O)CO)O)CCCC[NH3+])O)CCC(=O)O)O)CO)O)CCCC[NH3+])O)CCC(=O)O)O)CO)O)CCCC[NH3+])O)CCC(=O)O)[O-])CO)O)N=C(C(N=C(C1CCCN1C(=O)C(C(O)C)N=C(C(N=C(C(N=C(C(N=C(C(C(O)C)N=C(C(N=C(C(N=C(C([NH+]=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(C(C)C)N=C(C(C(CC)C)N=C(CN=C(C(N=C(C(N=C(C(N=C(C(N=C(C(N=C(C(C(C)C)N=C(C(N=C(C(N=C(C(N=C(C1CCCN1C(=O)C(N=C(C(N=C(C(N=C(C(N=C(C(Cc1ccccc1)N=C(CN=C(C(N=C(C(N=C(CN=C(C(N=C(C(C(C)C)N=C(C(N=C(C(N=C(C(N=C(C(C(O)C)N=C(C(N=C(C(C(C)C)N=C(C(N=C(C(N=C(CN=C(CN=C(C(N=C(C(N=C(C(C(O)C)N=C(C(N=C(C(N=C(C1CCCN1C(=O)C(N=C(C(N=C(C([NH3+])C)[O-])Cc1ccc(cc1)O)O)CCCNC(=[NH2+])N)[O-])CO)O)CCC(=O)O)O)[O-])CC(C)C)O)CS)O)O)O)CCC(=O)O)O)CC(C)C)O)O)CC(=O)O)O)O)CC(C)C)O)CCC(=[NH2+])[O-])O)Cc1ccccc1)O)O)CS)O)O)CC(=O)O)O)CCCNC(=[NH2+])N)O)O)O)Cc1ccc(cc1)O)O)Cc1ccccc1)O)CO)O)CCCNC(=[NH2+])N)[O-])C)O)CO)O)CCCNC(=[NH2+])N)O)O)CO)O)CCCNC(=[NH2+])N)O)CCCNC(=[NH2+])N)O)CO)O)CCCNC(=[NH2+])N)O)[O-])O)O)CCC(=O)O)O)CCC(=O)O)O)CS)O)CS)O)Cc1ccccc1)O)CCCNC(=[NH2+])N)O)CO)O)CS)O)CC(=O)[O-])O)CC(C)C)O)C)[O-])CC(C)C)O)CC(C)C)O)CCC(=O)O)O)O)Cc1ccc(cc1)O)O)CS)O)C)O)O)C)O", can);
	}
	
	@Test
	public void testValidInchi1() throws IOException {
		String inchi = "InChI=1S/C13H24O2/c1-3-4-5-6-7-8-9-10-11-12-15-13(2)14/h10-11H,3-9,12H2,1-2H3/b11-10+";
		String can = OpenBabelProcessWrapper.convert("inchi", "can", inchi);
		assertEquals("CCCCCCCC/C=C/COC(=O)C", can);
	}

}
