package edu.uminho.biosynth.core.data.io.remote;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.remote.BioCycRemoteSource;

public class TestBiocycRemoteSource {

	private static BioCycRemoteSource remote;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		BioCycRemoteSource.SAVETOCACHE = true;
		BioCycRemoteSource.LOCALCACHE = "D:/home/data/biocyc";
		remote = new BioCycRemoteSource("META");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testGetAllMetabolitesIds() {
		int elements = remote.getAllMetabolitesIds().size();
		System.out.println(elements);
		assertEquals(10856, elements);
	}
	
	@Test
	public void testGetMetaboliteCPD14641() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("CPD-14641");
		System.out.println(cpd);
		assertEquals(true, cpd.getParents().contains("Cardanols"));
	}
	
	@Test
	public void testGetMetabolitePROTON() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("PROTON");
		System.out.println(cpd);
		assertEquals(true, cpd.getParents().contains("a subatomic particle"));
	}
	
	@Test
	public void testGetMetaboliteCPD12940() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("CPD-12940");
		System.out.println(cpd);
		assertEquals(true, cpd.getParents().contains("Cardanols"));
	}
	
	@Test
	public void testGetMetaboliteGLYCOLITHOCHOLATE3SULFATES() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("GLYCOLITHOCHOLATE-3-SULFATES");
		System.out.println(cpd);
		assertEquals(true, cpd.getParents().contains("Cardanols"));
	}

	@Test
	public void testGetMetaboliteCPDQT326() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("CPDQT-326");
		System.out.println(cpd);
		assertEquals(true, cpd.getParents().contains("Cardanols"));
	}

	@Test
	public void testGetMetaboliteSUCCOA() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("SUC-COA");
		System.out.println(cpd);
		assertEquals("CC(C)(C(O)C(=O)NCCC(=O)NCCSC(CCC(=O)[O-])=O)COP(=O)(OP(=O)(OCC1(C(OP([O-])(=O)[O-])C(O)C(O1)N3(C2(=C(C(N)=NC=N2)N=C3))))[O-])[O-]", cpd.getSmiles());
	}

	@Test
	public void testGetMetaboliteXDGLCHEX15() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("X-DGLC-HEX-1:5");
		System.out.println(cpd);
		assertEquals("s", cpd.getSmiles());
	}
	
	@Test
	public void testGetMetaboliteCPDQT288() {
		BioCycMetaboliteEntity cpd = remote.getMetaboliteInformation("CPDQT-288");
		System.out.println(cpd);
		assertEquals("", cpd.getComment());
	}
}
