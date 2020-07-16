package pt.uminho.sysbio.biosynthframework.etl.biodb;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynth.integration.AbstractGraphEdgeEntity;
import pt.uminho.sysbio.biosynth.integration.AbstractGraphNodeEntity;
import pt.uminho.sysbio.biosynth.integration.GraphMetaboliteEntity;
import pt.uminho.sysbio.biosynth.integration.etl.CentralMetaboliteEtlDataCleansing;
import pt.uminho.sysbio.biosynth.integration.etl.EtlDataCleansing;
import pt.uminho.sysbio.biosynth.integration.etl.biodb.biocyc.BiocycMetaboliteTransform;
import pt.uminho.sysbio.biosynthframework.ReferenceType;
import pt.uminho.sysbio.biosynthframework.biodb.bigg.BiggMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteCrossreferenceEntity;
import pt.uminho.sysbio.biosynthframework.biodb.biocyc.BioCycMetaboliteEntity;
import pt.uminho.sysbio.biosynthframework.chemanalysis.cdk.CdkWrapper;
import pt.uminho.sysbio.biosynthframework.core.data.io.dao.biodb.ptools.biocyc.RestBiocycMetaboliteDaoImpl;
import pt.uminho.sysbio.biosynthframework.io.biodb.InternalBigg1MetaboliteDaoImpl;

public class TestBiocycMetaboliteTransform {

  private BiocycMetaboliteTransform transform;
  private static Map<String, String> biggInternalIdToEntryMap = new HashMap<> ();
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    InternalBigg1MetaboliteDaoImpl dao = new InternalBigg1MetaboliteDaoImpl();
    for (String e : dao.getAllEntries()) {
      BiggMetaboliteEntity cpd = dao.getByEntry(e);
      long iid = cpd.getInternalId();
      biggInternalIdToEntryMap.put(Long.toString(iid), cpd.getEntry());
    }
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    transform = new BiocycMetaboliteTransform("MetaCyc", biggInternalIdToEntryMap);
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testMetabolite1() {
    RestBiocycMetaboliteDaoImpl dao = new RestBiocycMetaboliteDaoImpl();
    dao.setLocalStorage("D:/var/biodb/biocyc");
    dao.setDatabaseVersion("21.1");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    dao.setPgdb("META");
    BioCycMetaboliteEntity e = dao.getMetaboliteByEntry("META:CDPDIACYLGLYCEROL");
    transform.apply(e);
    fail("Not yet implemented");
  }
  
  @Test
  public void testMetabolite2() {
    BioCycMetaboliteEntity cpd = new BioCycMetaboliteEntity();
    cpd.setEntry("META:FRU");
    List<BioCycMetaboliteCrossreferenceEntity> references = new ArrayList<>();
    BioCycMetaboliteCrossreferenceEntity xref1 = 
        new BioCycMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "BIGG", "33835");
    xref1.setUrl("http://bigg.ucsd.edu/universal/metabolites/33835");
    BioCycMetaboliteCrossreferenceEntity xref2 = 
        new BioCycMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "CAS", "30237-26-4");
    xref2.setUrl("http://www.commonchemistry.org/ChemicalDetail.aspx?ref=30237-26-4");
    BioCycMetaboliteCrossreferenceEntity xref3 = 
        new BioCycMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "CHEBI", "15824");
    xref3.setUrl("http://www.ebi.ac.uk/chebi/searchId.do?chebiId=CHEBI:15824");
    BioCycMetaboliteCrossreferenceEntity xref4 = 
        new BioCycMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "CAS", "57-48-7");
    xref4.setUrl("http://www.commonchemistry.org/ChemicalDetail.aspx?ref=57-48-7");
    BioCycMetaboliteCrossreferenceEntity xref5 = 
        new BioCycMetaboliteCrossreferenceEntity(ReferenceType.DATABASE, "LIGAND-CPD", "C00095");
    xref5.setUrl("http://www.genome.ad.jp/dbget-bin/www_bget?C00095");
    references.add(xref1);
    references.add(xref2);
    references.add(xref3);
    references.add(xref4);
    references.add(xref5);
    cpd.setCrossReferences(references);
    
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
    assertNotNull(gcpd);
    assertEquals("META:FRU", gcpd.getEntry());
    assertTrue(gcpd.getConnectedEntities().containsKey("has_crossreference_to"));
    assertEquals(5, gcpd.getConnectedEntities().get("has_crossreference_to").size());
  }
  
  @Test
  public void testMetabolite3() {
    RestBiocycMetaboliteDaoImpl dao = new RestBiocycMetaboliteDaoImpl();
    dao.setLocalStorage("D:/var/biodb/biocyc");
    dao.setDatabaseVersion("21.1");
    dao.setUseLocalStorage(true);
    dao.setSaveLocalStorage(true);
    dao.setPgdb("META");
//  BioCycMetaboliteEntity cpd = dao.getMetaboliteByEntry("ACP");
    
//  BioCycMetaboliteEntity cpd = dao.getMetaboliteByEntry("CPD-4185");
    BioCycMetaboliteEntity cpd = dao.getMetaboliteByEntry("Cytochromes-c553");
    GraphMetaboliteEntity gcpd = transform.apply(cpd);
    System.out.println(cpd.getEntry() + " " + cpd.getName());
    System.out.println("\t" + gcpd.getConnectionTypeCounter());
    for (String l : gcpd.getConnectedEntities().keySet()) {
//      int k = 0;
      for (Pair<AbstractGraphEdgeEntity, AbstractGraphNodeEntity> p : gcpd.getConnectedEntities().get(l)) {
        System.out.println("\t" + l + "\t" + p.getLeft().getProperties() + " ==> "+ p);
//        k++;
      }
    }
    EtlDataCleansing<GraphMetaboliteEntity> c = new CentralMetaboliteEtlDataCleansing(new CdkWrapper());
    c.etlCleanse(gcpd);
//  try {
//  Map<String, Triple<String, String, EtlCleasingType>> dc = dataCleansing.etlCleanse(cpd);
//  for (String k : dc.keySet()) {
//    System.out.println("\t" + k + "\t" + dc.get(k));
//  }
//} catch (Exception ee) {
//  ee.printStackTrace();
//}
    fail("Not yet implemented");
  }
}
