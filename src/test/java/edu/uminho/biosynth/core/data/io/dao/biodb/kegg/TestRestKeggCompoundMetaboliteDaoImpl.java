package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import static org.junit.Assert.*;

import java.io.Serializable;

import org.junit.BeforeClass;
import org.junit.Test;

import edu.uminho.biosynth.core.components.biodb.kegg.KeggCompoundMetaboliteEntity;
import edu.uminho.biosynth.core.data.io.dao.MetaboliteDao;


public class TestRestKeggCompoundMetaboliteDaoImpl {

	private static double EPSILON = 0.00000001;
	private static MetaboliteDao<KeggCompoundMetaboliteEntity> metaboliteDao;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RestKeggCompoundMetaboliteDaoImpl metaboliteDaoImpl = new RestKeggCompoundMetaboliteDaoImpl();
		metaboliteDaoImpl.setSaveLocalStorage(true);
		metaboliteDaoImpl.setUseLocalStorage(true);
		metaboliteDaoImpl.setLocalStorage("D:/home/data/kegg/");
		
		metaboliteDao = metaboliteDaoImpl;
	}
	
	@Test
	public void testParseAll() {

		for (Serializable cpdId : metaboliteDao.getAllMetaboliteIds()) {
			KeggCompoundMetaboliteEntity cpd = metaboliteDao.getMetaboliteById(cpdId);
			assertNotEquals(null, cpd);
			assertEquals(cpdId, cpd.getEntry());
			System.out.println(cpd.getEntry());
		}
	}

	@Test
	public void test_C00775() {
		
		KeggCompoundMetaboliteEntity cpd = metaboliteDao.getMetaboliteByEntry("C00755");

		assertEquals("C00755", cpd.getEntry());
		assertEquals("4-Hydroxy-3-methoxy-benzaldehyde;Vanillin;Vanillaldehyde;4-Hydroxy-3-methoxybenzaldehyde", cpd.getName());
		assertEquals("C8H8O3", cpd.getFormula());
		assertEquals(152.0473, cpd.getMass(), EPSILON);
		assertEquals(5, cpd.getEnzymes().size());
		assertEquals("Same as: D00091", cpd.getRemark());
		assertEquals(null, cpd.getComment());
		assertEquals(null, cpd.getInchi());
		assertEquals(6, cpd.getReactions().size());
		assertEquals(8, cpd.getCrossReferences().size());
		assertNotEquals(true, cpd.getMol2d());
	}
	
	@Test
	public void test_C01356() {
		
		KeggCompoundMetaboliteEntity cpd = metaboliteDao.getMetaboliteByEntry("C01456");

		assertEquals("C01456", cpd.getEntry());
		assertEquals("Tropate;Tropic acid;alpha-(Hydroxymethyl)phenylacetic acid", cpd.getName());
		assertEquals("C9H10O3", cpd.getFormula());
		assertEquals(166.063, cpd.getMass(), EPSILON);
		assertEquals(1, cpd.getEnzymes().size());
		assertEquals(null, cpd.getRemark());
		assertEquals(null, cpd.getComment());
		assertEquals(null, cpd.getInchi());
		assertEquals(3, cpd.getReactions().size());
		assertEquals(8, cpd.getCrossReferences().size());
		assertNotEquals(true, cpd.getMol2d());
	}
//	
//	@Test
//	public void testD00063() {
//		RestKeggDrugMetaboliteDaoImpl dao = new RestKeggDrugMetaboliteDaoImpl();
//		dao.setLocalStorage("D:/home/data/kegg/");
//		dao.setSaveLocalStorage(true);
//		dao.setUseLocalStorage(true);
//		
//		KeggDrugMetaboliteEntity cpd = dao.getMetaboliteInformation("D00063");
//
//		System.out.println(cpd);
//		assertEquals("D00063", cpd.getEntry());
//		assertEquals("C18H37N5O9", cpd.getFormula());
//		assertEquals("Antibacterial", cpd.getActivity());
//		assertEquals("16S rRNA of 30S ribosomal subunit, protein synthesis inhibitor [KO:K01977]\n  PATHWAY   ko03010(K01977)  Ribosome", cpd.getTarget());
//		assertEquals(null, cpd.getMetabolism());
//		assertEquals(true, cpd.getProduct() != null);
//		assertEquals(true, cpd.getMol2d() != null);
//		assertEquals("Streptomyces tenebrarius [TAX:1933]", cpd.getDrugSource());
//	}
//	
//	@Test
//	public void testD00092() {
//		RestKeggDrugMetaboliteDaoImpl dao = new RestKeggDrugMetaboliteDaoImpl();
//		dao.setLocalStorage("D:/home/data/kegg/");
//		dao.setSaveLocalStorage(true);
//		dao.setUseLocalStorage(true);
//		
//		KeggDrugMetaboliteEntity cpd = dao.getMetaboliteInformation("D00092");
//
//		System.out.println(cpd);
//		assertEquals("D00092", cpd.getEntry());
//		assertEquals(null, cpd.getFormula());
//		assertEquals(null, cpd.getActivity());
//		assertEquals(null, cpd.getTarget());
//		assertEquals(null, cpd.getMetabolism());
//		assertEquals(null, cpd.getProduct());
//		assertEquals(true, cpd.getMol2d() != null);
//		assertEquals("Coptis japonica [TAX:3442], Coptis chinensis [TAX:261450], Coptis deltoidea [TAX:261449], Coptis teeta [TAX:261448]", cpd.getDrugSource());
//		assertEquals("Berberine [CPD:C00757], Palmatine [CPD:C05315], Jateorrhizine [CPD:C09553], Coptisine [CPD:C16938], Worenine [CPD:C17083], Magnoflorine [CPD:C09581], Ferulic acid [CPD:C01494], Chlorogenic acid [CPD:C00852], Tetrahydroberberine [CPD:C03329], Tetrahydropalmatine [CPD:C02890]", cpd.getComponent());
//	}
//	
//	@Test
//	public void testD00085() {
//		//D00023 str_map
//		RestKeggDrugMetaboliteDaoImpl dao = new RestKeggDrugMetaboliteDaoImpl();
//		dao.setLocalStorage("D:/home/data/kegg/");
//		dao.setSaveLocalStorage(true);
//		dao.setUseLocalStorage(true);
//		
//		KeggDrugMetaboliteEntity cpd = dao.getMetaboliteInformation("D00085");
//
//		System.out.println(cpd);
//		assertEquals("D00085", cpd.getEntry());
//		assertEquals(null, cpd.getFormula());
//		assertEquals("Antidiabetic [DS:H00408 H00409 H00410]", cpd.getActivity());
//		assertEquals(true, cpd.getTarget() != null);
//		assertEquals(null, cpd.getMetabolism());
//		assertEquals(null, cpd.getProduct());
//		assertEquals(true, cpd.getMol2d() != null);
//		assertEquals(true, cpd.getSequence() != null);
//		assertEquals(null, cpd.getDrugSource());
//		assertEquals(null, cpd.getComponent());
//	}

}
