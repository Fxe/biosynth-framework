package pt.uminho.sysbio.biosynthframework.io;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import pt.uminho.sysbio.biosynthframework.biodb.hmdb.HmdbMetaboliteEntity;

public class TestXmlHmdbMetaboliteDaoImpl {

  private MetaboliteDao<HmdbMetaboliteEntity> dao;
  
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
    dao = new XmlHmdbMetaboliteDaoImpl("/var/biodb/hmdb/3.6");
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void test_list_entries() {
    int total = dao.getAllMetaboliteEntries().size();
    assertEquals(41514, total);
  }

  @Test
  public void test_get_entry_HMDB00001() {
//    HmdbMetaboliteEntity cpd = dao.getMetaboliteByEntry("HMDB00001");
//    System.out.println(cpd.getCrossreferences());
//    System.out.println(cpd.getOntology());
//    System.out.println(cpd.ontology.cellular_locations);
//    System.out.println(cpd.ontology.biofunctions);
//    System.out.println(cpd);
  }
  
//  @Test
//  public void test_get_all_entry() {
//    int read = 0;
//    int tread = 0;
//    long tstart = System.currentTimeMillis();
//    long start = System.currentTimeMillis();
//    List<HmdbMetaboliteEntity> db = new ArrayList<> ();
//    for (String entry : dao.getAllMetaboliteEntries()) {
//      HmdbMetaboliteEntity cpd = dao.getMetaboliteByEntry(entry);
//      db.add(cpd);
//      read++;
//      tread++;
//      if (read % 5000 == 0) {
//        long end = System.currentTimeMillis();
//        double time = (double)(end - start) / 1000;
//        System.out.println(((double)read / time) + " r/s");
//        read = 0;
//        start = System.currentTimeMillis();
//      }
//    }
//    long tend = System.currentTimeMillis();
//    double time = (double)(tend - tstart) / 1000;
//    System.out.println(((double)tread / time) + " r/s");
//  }
//
//  @Test
//  public void test_get_fail_entry() {
//    HmdbMetaboliteEntity cpd = dao.getMetaboliteByEntry("HMDBXXXXX");
//    assertNull(cpd);
//  }
}
