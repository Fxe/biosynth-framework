package edu.uminho.biosynth.core.data.io.dao.biodb.kegg;

import static org.junit.Assert.*;

import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.kegg.KeggDrugMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.kegg.RestKeggDrugMetaboliteDaoImpl;

public class TestRestKeggDrugMetaboliteDaoImpl {

//	@Test
//	public void testD00066() {
//		RestKeggDrugMetaboliteDaoImpl dao = new RestKeggDrugMetaboliteDaoImpl();
//		dao.setLocalStorage("D:/home/data/kegg/");
//		dao.setSaveLocalStorage(true);
//		dao.setUseLocalStorage(true);
//		
//		KeggDrugMetaboliteEntity cpd = dao.getMetaboliteById("D00066");
//
//		System.out.println(cpd);
//		assertEquals("D00066", cpd.getEntry());
//		assertEquals("C21H30O2", cpd.getFormula());
//		assertEquals("Progestin", cpd.getActivity());
//		assertEquals(true, cpd.getTarget() != null);
//		assertEquals("Enzyme: CYP3A4 [HSA:1576], CYP2C19 [HSA:1557]", cpd.getMetabolism());
//		assertEquals(true, cpd.getProduct() != null);
//		assertEquals(true, cpd.getMol2d() != null);
//		
//	}
//	
//	@Test
//	public void testD00063() {
//		RestKeggDrugMetaboliteDaoImpl dao = new RestKeggDrugMetaboliteDaoImpl();
//		dao.setLocalStorage("D:/home/data/kegg/");
//		dao.setSaveLocalStorage(true);
//		dao.setUseLocalStorage(true);
//		
//		KeggDrugMetaboliteEntity cpd = dao.getMetaboliteById("D00063");
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
//		KeggDrugMetaboliteEntity cpd = dao.getMetaboliteById("D00092");
//
//		System.out.println(cpd);
//		assertEquals("D00092", cpd.getEntry());
//		assertEquals(null, cpd.getFormula());
//		assertEquals(null, cpd.getActivity());
//		assertEquals(null, cpd.getTarget());
//		assertEquals(null, cpd.getMetabolism());
//		assertEquals(null, cpd.getProduct());
//		assertEquals(true, cpd.getMol2d() == null);
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
//		KeggDrugMetaboliteEntity cpd = dao.getMetaboliteById("D00085");
//
//		System.out.println(cpd);
//		assertEquals("D00085", cpd.getEntry());
//		assertEquals(null, cpd.getFormula());
//		assertEquals("Antidiabetic [DS:H00408 H00409 H00410]", cpd.getActivity());
//		assertEquals(true, cpd.getTarget() != null);
//		assertEquals(null, cpd.getMetabolism());
//		assertEquals(null, cpd.getProduct());
//		assertEquals(true, cpd.getMol2d() == null);
//		assertEquals(true, cpd.getSequence() != null);
//		assertEquals(null, cpd.getDrugSource());
//		assertEquals(null, cpd.getComponent());
//	}

}
