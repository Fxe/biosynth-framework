package pt.uminho.sysbio.biosynth.integration.etl.biodb;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.io.dao.neo4j.MetaboliteMajorLabel;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteOntology;

public class TestHmdbMetaboliteTransform {

  private HmdbMetaboliteTransform transform;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    Map<String, String> mapping = new HashMap<> ();
    
    transform = new HmdbMetaboliteTransform(mapping);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test_basic_entry_and_label() {
    HmdbMetaboliteEntity cpd = new HmdbMetaboliteEntity();
    cpd.setEntry("HMDB00001");
    GraphMetaboliteEntity gcpd = transform.etlTransform(cpd);
    
    assertEquals("HMDB00001", gcpd.getEntry());
    assertEquals(MetaboliteMajorLabel.HMDB.toString(), gcpd.getMajorLabel());
  }

  @Test
  public void test_HMDB00001() {
    HmdbMetaboliteOntology ontology = new HmdbMetaboliteOntology();
    ontology.setStatus("Detected and Quantified");
    ontology.getOrigins().add("Endogenous");
    ontology.getBiofunctions().add("Protein synthesis, amino acid biosynthesis");
    ontology.getCellularLocations().add("Cytoplasm");
    HmdbMetaboliteEntity cpd = new HmdbMetaboliteEntity();
    cpd.setEntry("HMDB00001");
    cpd.setName("1 methylhistidine");
    cpd.setAverageMolecularWeight(169.1811);
    cpd.setMonisotopicMoleculateWeight(169.085126611);
    cpd.setIupacName("(2S)-2-amino-3-(1-methyl-1H-imidazol-4-yl)propanoic acid");
    cpd.setSmiles("CN1C=NC(C[C@H](N)C(O)=O)=C1");
    cpd.setInchi("InChI=1S/C7H11N3O2/c1-10-3-5(9-4-10)2-6(8)7(11)12/h3-4,6H,2,8H2,1H3,(H,11,12)/t6-/m0/s1");
    cpd.setInchikey("InChIKey=BRMWTNUJHUMWMS-LURJTMIESA-N");
    cpd.getSynonyms().add("1 Methylhistidine");
    cpd.getSynonyms().add("1-Methyl histidine");
    cpd.getSynonyms().add("1-N-Methyl-L-histidine");
    cpd.setOntology(ontology);
    cpd.addCrossReference(new HmdbMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "kegg_id", "C01152"));
    cpd.addCrossReference(new HmdbMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "pubchem_compound_id", "92105"));
    cpd.addCrossReference(new HmdbMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "chebi_id", "50599"));
    cpd.addCrossReference(new HmdbMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "cas_registry_number", "332-80-9"));
    cpd.addCrossReference(new HmdbMetaboliteCrossreferenceEntity(
        ReferenceType.DATABASE, "biocyc_id", "CPD-1823"));
    cpd.getBiofluids().add("Blood");
    cpd.getBiofluids().add("Cerebrospinal Fluid (CSF)");
    cpd.getBiofluids().add("Saliva");
    cpd.getBiofluids().add("Urine");
    cpd.getTissues().add("Muscle");
    cpd.getTissues().add("Skeletal Muscle");
    
    GraphMetaboliteEntity gcpd = transform.etlTransform(cpd);
    assertEquals("HMDB00001", gcpd.getEntry());
    assertEquals(MetaboliteMajorLabel.HMDB.toString(), gcpd.getMajorLabel());
  }
}
