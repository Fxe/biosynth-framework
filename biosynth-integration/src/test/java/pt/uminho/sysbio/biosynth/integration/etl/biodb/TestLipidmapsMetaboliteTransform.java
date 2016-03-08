package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.lipidmap.LipidmapsMetaboliteEntity;

public class TestLipidmapsMetaboliteTransform {

  private LipidmapsMetaboliteTransform transform;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    transform = new LipidmapsMetaboliteTransform();
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test_basic_entry_and_label() {
    LipidmapsMetaboliteEntity cpd = new LipidmapsMetaboliteEntity();
    cpd.setEntry("LMST01010168");
    GraphMetaboliteEntity gcpd = transform.etlTransform(cpd);
    
    assertEquals("LMST01010168", gcpd.getEntry());
    assertEquals(MetaboliteMajorLabel.LipidMAPS.toString(), gcpd.getMajorLabel());
  }
  
  @Test
  public void test_transform_LMST01010168() {
    LipidmapsMetaboliteEntity cpd = new LipidmapsMetaboliteEntity();
    cpd.setEntry("LMST01010168");
    cpd.setName("Zymosterone");
    cpd.setFormula("C27H42O");
    cpd.setSystematicName("5alpha-cholesta-8,24-dien-3-one");
    cpd.setExactMass(382.323566);
    cpd.setInchiKey("AUNLIRXIJAVBNM-ZSBATXSLSA-N");
    cpd.setPubchemSubstanceUrl("http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=85300602");
    cpd.setLipidMapsCmpdUrl("http://www.lipidmaps.org/data/LMSDRecord.php?LMID=LMST01010168");
    cpd.setStatus("Active");
    cpd.setActive(true);
    cpd.setGenerated(false);
    cpd.setCategory("Sterol Lipids [ST]");
    cpd.setMainClass("Sterols [ST01]");
    cpd.setSubSlass("Cholesterol and derivatives [ST0101]");
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "PUBCHEM_SID", "85300602"));
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "PUBCHEM_CID", "22298942"));
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "CHEBI_ID", "52386"));
    
    GraphMetaboliteEntity gcpd = transform.etlTransform(cpd);
    assertEquals("LMST01010168", gcpd.getEntry());
    assertEquals(MetaboliteMajorLabel.LipidMAPS.toString(), gcpd.getMajorLabel());
  }

  @Test
  public void test_transform_LMPR01070011() {
    LipidmapsMetaboliteEntity cpd = new LipidmapsMetaboliteEntity();
    cpd.setEntry("LMPR01070011");
    cpd.setName("alpha-Carotene/ beta,epsilon-Carotene");
    cpd.setFormula("C40H56");
    cpd.setExactMass(536.438201);
    cpd.setInchiKey("ANVAOWXLWRTKGA-NTXLUARGSA-N");
    cpd.setPubchemSubstanceUrl("http://pubchem.ncbi.nlm.nih.gov/summary/summary.cgi?sid=24701838");
    cpd.setLipidMapsCmpdUrl("http://www.lipidmaps.org/data/LMSDRecord.php?LMID=LMPR01070011");
    cpd.setStatus("Active");
    cpd.setActive(true);
    cpd.setGenerated(false);
    cpd.setCategory("Prenol Lipids [PR]");
    cpd.setMainClass("Isoprenoids [PR01]");
    cpd.setSubSlass("C40 isoprenoids (tetraterpenes) [PR0107]");
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "PUBCHEM_SID", "24701838"));
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "PUBCHEM_CID", "6419725"));
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "CHEBI_ID", "28425"));
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "LIPIDBANK_ID", "VCA0009"));
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "KEGG_ID", "C05433"));
    cpd.addCrossReference(new LipidmapsMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "HMDBID", "HMDB03993"));
    GraphMetaboliteEntity gcpd = transform.etlTransform(cpd);
    assertEquals("LMPR01070011", gcpd.getEntry());
    assertEquals(MetaboliteMajorLabel.LipidMAPS.toString(), gcpd.getMajorLabel());
  }
}
